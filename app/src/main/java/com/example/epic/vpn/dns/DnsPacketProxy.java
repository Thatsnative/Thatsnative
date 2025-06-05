package com.example.epic.vpn.dns;

import android.content.Context;
import android.util.Log;

import com.example.epic.AdAwayApplication;
import com.example.epic.analityc.AnalyticLogger;
import com.example.epic.db.entity.HostEntry;
import com.example.epic.db.entity.ListType;
import com.example.epic.db.entity.RuleEntity;
import com.example.epic.db.entity.SourceBlockType;
import com.example.epic.helper.PreferenceHelper;
import com.example.epic.model.vpn.VpnModel;
import com.example.epic.vpn.dns.DNS.AAAARecord;
import com.example.epic.vpn.dns.DNS.ARecord;
import com.example.epic.vpn.dns.DNS.DClass;
import com.example.epic.vpn.dns.DNS.Flags;
import com.example.epic.vpn.dns.DNS.Message;
import com.example.epic.vpn.dns.DNS.Name;
import com.example.epic.vpn.dns.DNS.Rcode;
import com.example.epic.vpn.dns.DNS.SOARecord;
import com.example.epic.vpn.dns.DNS.Section;
import com.example.epic.vpn.dns.DNS.TextParseException;

import org.pcap4j.packet.IpPacket;
import org.pcap4j.packet.IpSelector;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.IpV4Rfc791Tos;
import org.pcap4j.packet.IpV6Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;
import org.pcap4j.packet.UnknownPacket;
import org.pcap4j.packet.namednumber.IpNumber;
import org.pcap4j.packet.namednumber.IpVersion;
import org.pcap4j.packet.namednumber.TcpPort;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

import timber.log.Timber;

/**
 * Creates and parses packets, and sends packets to a remote socket or the device using VpnWorker.
 */
public class DnsPacketProxy {
    // Choose a value that is smaller than the time needed to unblock a host.
    private static final int NEGATIVE_CACHE_TTL_SECONDS = 5;
    private static final SOARecord NEGATIVE_CACHE_SOA_RECORD;

    static {
        try {
            // Let's use a guaranteed invalid hostname here, clients are not supposed to use
            // our fake values, the whole thing just exists for negative caching.
            Name name = new Name("adaway.vpn.invalid.");
            NEGATIVE_CACHE_SOA_RECORD = new SOARecord(name, DClass.IN, NEGATIVE_CACHE_TTL_SECONDS,
                    name, name, 0, 0, 0, 0, NEGATIVE_CACHE_TTL_SECONDS);
        } catch (TextParseException e) {
            throw new RuntimeException(e);
        }
    }

    private final EventLoop eventLoop;
    private final DnsServerMapper dnsServerMapper;
    private VpnModel vpnModel;
    private Context context;

    public DnsPacketProxy(EventLoop eventLoop, DnsServerMapper dnsServerMapper) {
        this.eventLoop = eventLoop;
        this.dnsServerMapper = dnsServerMapper;
    }

    /**
     * Initializes the rules database and the list of upstream servers.
     *
     * @param context The context we are operating in (for the database).
     */
    public void initialize(Context context) {
        this.vpnModel = (VpnModel) ((AdAwayApplication) context.getApplicationContext()).getAdBlockModel();
        this.context = context;
    }

    /**
     * Handles a responsePayload from an upstream DNS server
     *
     * @param requestPacket   The original request packet
     * @param responsePayload The payload of the response
     */
    public void handleDnsResponse(IpPacket requestPacket, byte[] responsePayload) {
        UdpPacket udpOutPacket = (UdpPacket) requestPacket.getPayload();
        UdpPacket.Builder payLoadBuilder = new UdpPacket.Builder(udpOutPacket)
                .srcPort(udpOutPacket.getHeader().getDstPort())
                .dstPort(udpOutPacket.getHeader().getSrcPort())
                .srcAddr(requestPacket.getHeader().getDstAddr())
                .dstAddr(requestPacket.getHeader().getSrcAddr())
                .correctChecksumAtBuild(true)
                .correctLengthAtBuild(true)
                .payloadBuilder(
                        new UnknownPacket.Builder().rawData(responsePayload)
                );

        IpPacket ipOutPacket;
        if (requestPacket instanceof IpV4Packet) {
            ipOutPacket = new IpV4Packet.Builder((IpV4Packet) requestPacket)
                    .srcAddr((Inet4Address) requestPacket.getHeader().getDstAddr())
                    .dstAddr((Inet4Address) requestPacket.getHeader().getSrcAddr())
                    .correctChecksumAtBuild(true)
                    .correctLengthAtBuild(true)
                    .payloadBuilder(payLoadBuilder)
                    .build();

        } else {
            ipOutPacket = new IpV6Packet.Builder((IpV6Packet) requestPacket)
                    .srcAddr((Inet6Address) requestPacket.getHeader().getDstAddr())
                    .dstAddr((Inet6Address) requestPacket.getHeader().getSrcAddr())
                    .correctLengthAtBuild(true)
                    .payloadBuilder(payLoadBuilder)
                    .build();
        }

        this.eventLoop.queueDeviceWrite(ipOutPacket);
    }

    /**
     * Handles a DNS request, by either blocking it or forwarding it to the remote location.
     *
     * @param packetData The packet data to read
     * @throws IOException If some network error occurred
     */
    public void handleDnsRequest(byte[] packetData) throws IOException {
        IpPacket ipPacket;
        try {
            ipPacket = (IpPacket) IpSelector.newPacket(packetData, 0, packetData.length);
        } catch (Exception e) {
            Timber.i(e, "handleDnsRequest: Discarding invalid IP packet");
            return;
        }

        // handle TCP protocol
        if (ipPacket.getHeader().getProtocol() != IpNumber.UDP) {
            ListType type = ListType.ALLOWED;
            try {
                Packet payload = ipPacket.getPayload();
                if (!(payload instanceof TcpPacket)) return;
                TcpPacket tcpPacket = (TcpPacket) payload;
                type = handleTcpPacket(ipPacket, tcpPacket);
            } catch (Throwable e) {
                Log.w("handleDnsRequest", "handleTcpPacket failed");
            }
            if (type == ListType.BLOCKED) {
                AnalyticLogger.INSTANCE.info("handleDnsRequest: TCP Name " + ipPacket.getHeader().getDstAddr() + " blocked by rule!");
                PreferenceHelper.incrementAdBlockCount(context);
                return;
            }
        }

        // Check UDP protocol
        if (ipPacket.getHeader().getProtocol() != IpNumber.UDP) {
            return;
        }

        UdpPacket updPacket;
        Packet udpPayload;

        try {
            updPacket = (UdpPacket) ipPacket.getPayload();
            udpPayload = updPacket.getPayload();
        } catch (Exception e) {
            Timber.i(e, "handleDnsRequest: Discarding unknown packet type %s", ipPacket.getHeader());
            return;
        }

        InetAddress packetAddress = ipPacket.getHeader().getDstAddr();
        int packetPort = updPacket.getHeader().getDstPort().valueAsInt();
        Optional<InetAddress> dnsAddressOptional = this.dnsServerMapper.getDnsServerFromFakeAddress(packetAddress);
        if (!dnsAddressOptional.isPresent()) {
            Timber.w("Cannot find mapped DNS for %s.", packetAddress.getHostAddress());
            return;
        }
        InetAddress dnsAddress = dnsAddressOptional.get();

        if (udpPayload == null) {
            Timber.i("handleDnsRequest: Sending UDP packet without payload: %s", updPacket);

            // Let's be nice to Firefox. Firefox uses an empty UDP packet to
            // the gateway to reduce the RTT. For further details, please see
            // https://bugzilla.mozilla.org/show_bug.cgi?id=888268
            DatagramPacket outPacket = new DatagramPacket(new byte[0], 0, dnsAddress, packetPort);
            eventLoop.forwardPacket(outPacket);
            return;
        }

        byte[] dnsRawData = udpPayload.getRawData();
        Message dnsMsg;
        try {
            dnsMsg = new Message(dnsRawData);
        } catch (IOException e) {
            Timber.i(e, "handleDnsRequest: Discarding non-DNS or invalid packet");
            return;
        }
        if (dnsMsg.getQuestion() == null) {
            Timber.i("handleDnsRequest: Discarding DNS packet with no query %s", dnsMsg);
            return;
        }
        Name name = dnsMsg.getQuestion().getName();
        String dnsQueryName = name.toString(true);
        HostEntry entry = getHostEntry(dnsQueryName);
        switch (entry.getType()) {
            case BLOCKED:
                AnalyticLogger.INSTANCE.info("handleDnsRequest: DNS Name " + dnsQueryName + " blocked!");
                PreferenceHelper.incrementAdBlockCount(this.context);
                dnsMsg.getHeader().setFlag(Flags.QR);
                dnsMsg.getHeader().setRcode(Rcode.NOERROR);
                dnsMsg.addRecord(NEGATIVE_CACHE_SOA_RECORD, Section.AUTHORITY);
                handleDnsResponse(ipPacket, dnsMsg.toWire());
                break;
            case ALLOWED:
                AnalyticLogger.INSTANCE.info("handleDnsRequest: DNS Name " + dnsQueryName + " allowed, sending to " + dnsAddress);
                DatagramPacket outPacket = new DatagramPacket(dnsRawData, 0, dnsRawData.length, dnsAddress, packetPort);
                this.eventLoop.forwardPacket(outPacket, data -> handleDnsResponse(ipPacket, data));
                break;
            case REDIRECTED:
                AnalyticLogger.INSTANCE.info("handleDnsRequest: DNS Name " + dnsQueryName + " redirected to " + entry.getRedirection());
                dnsMsg.getHeader().setFlag(Flags.QR);
                dnsMsg.getHeader().setFlag(Flags.AA);
                dnsMsg.getHeader().unsetFlag(Flags.RD);
                dnsMsg.getHeader().setRcode(Rcode.NOERROR);
                try {
                    InetAddress address = InetAddress.getByName(entry.getRedirection());
                    com.example.epic.vpn.dns.DNS.Record record;
                    if (address instanceof Inet6Address) {
                        record = new AAAARecord(name, DClass.IN, NEGATIVE_CACHE_TTL_SECONDS, address);
                    } else {
                        record = new ARecord(name, DClass.IN, NEGATIVE_CACHE_TTL_SECONDS, address);
                    }
                    dnsMsg.addRecord(record, Section.ANSWER);
                } catch (UnknownHostException e) {
                    Log.e("UnknownHostException", "Failed to get inet address for host " + dnsQueryName, e);
                }
                handleDnsResponse(ipPacket, dnsMsg.toWire());
                break;
        }
    }

    public ListType handleTcpPacket(IpPacket ipPacket, TcpPacket tcpPacket) throws IOException {
        InetAddress srcAddress = ipPacket.getHeader().getSrcAddr();
        InetAddress dstAddress = ipPacket.getHeader().getDstAddr();
        int srcPort = tcpPacket.getHeader().getSrcPort().valueAsInt();
        int dstPort = tcpPacket.getHeader().getDstPort().valueAsInt();

        Packet payload = tcpPacket.getPayload();
        if (payload == null) {
            return ListType.ALLOWED;
        }
        byte[] rawData = payload.getRawData();

        String httpText = new String(rawData, StandardCharsets.UTF_8);
        String[] lines = httpText.split("\\r?\\n");

        if (lines.length == 0) {
            return ListType.ALLOWED;
        }
        String requestLine = lines[0];
        String hostHeader = null;
        for (String line : lines) {
            if (line.toLowerCase().startsWith("host:")) {
                hostHeader = line;
                break;
            }
        }

        if (hostHeader == null || !requestLine.contains("HTTP/")) {
            return ListType.ALLOWED;
        }

        String host = hostHeader.substring(hostHeader.indexOf(":") + 1).trim();
        String[] requestParts = requestLine.split(" ");
        String path = "/";
        if (requestParts.length > 1) {
            path = requestParts[1];
        }

        // Можно менять http/https по логике, здесь просто https
        String url = "https://" + host + path;

        RuleEntity rule = getRuleEntity(url);
        if (rule != null && rule.getType() == ListType.BLOCKED) {
            Log.i("Blocked request to %s by rule %s", "to" + url + "by" + rule.getPattern());

            // Отправляем 403 Forbidden заглушку
            sendHttpForbiddenResponse(ipPacket, srcAddress, srcPort, dstAddress, dstPort, tcpPacket);
            return ListType.BLOCKED;
        }
        return ListType.ALLOWED;
    }


    private HostEntry getHostEntry(String dnsQueryName) {
        String hostname = dnsQueryName.toLowerCase(Locale.ENGLISH);
        HostEntry entry = null;
        if (this.vpnModel != null) {
            entry = this.vpnModel.getEntry(hostname);
        }
        if (entry == null) {
            entry = new HostEntry();
            entry.setHost(hostname);
            entry.setType(ListType.ALLOWED);
        }
        return entry;
    }

    private RuleEntity getRuleEntity(String dnsQueryName) {
        String hostname = dnsQueryName.toLowerCase(Locale.ENGLISH);
        RuleEntity rule = null;
        if (this.vpnModel != null) {
            rule = this.vpnModel.getRuleEntry(hostname);
        }
        if (rule == null) {
            rule = new RuleEntity(
                    "",
                    ListType.ALLOWED,
                    SourceBlockType.ADBLOCK.name(),
                    null
            );
        }
        return rule;
    }

    /**
     * Interface abstracting away VpnWorker.
     */
    public interface EventLoop {
        /**
         * Forward a packet to the VPN underlying network.
         *
         * @param packet The packet to forward.
         * @throws IOException If the packet could not be forwarded.
         */
        void forwardPacket(DatagramPacket packet) throws IOException;

        /**
         * Forward a packet to the VPN underlying network.
         *
         * @param packet   The packet to forward.
         * @param callback The callback to call with the packet response data.
         * @throws IOException If the packet could not be forwarded.
         */
        void forwardPacket(DatagramPacket packet, Consumer<byte[]> callback) throws IOException;

        /**
         * Write an IP packet to the local TUN device
         *
         * @param packet The packet to write (a response to a DNS request)
         */
        void queueDeviceWrite(IpPacket packet);

        void sendToClient(byte[] data, int offset, int length);
    }

    public void sendHttpForbiddenResponse(IpPacket ipPacket,
                                          InetAddress srcAddress, int srcPort,
                                          InetAddress dstAddress, int dstPort,
                                          TcpPacket originalTcpPacket) {
        try {
            String httpResponse = "HTTP/1.1 403 Forbidden\r\n" +
                    "Content-Length: 0\r\n" +
                    "Connection: close\r\n\r\n";
            byte[] responseData = httpResponse.getBytes(StandardCharsets.UTF_8);

            // Построение TCP ответа
            TcpPacket.Builder tcpBuilder = new TcpPacket.Builder();
            tcpBuilder.srcPort(new TcpPort((short) dstPort, ""))
                    .dstPort(new TcpPort((short) srcPort, ""))
                    .sequenceNumber(originalTcpPacket.getHeader().getAcknowledgmentNumber())
                    .acknowledgmentNumber(
                            originalTcpPacket.getHeader().getSequenceNumber() +
                                    (originalTcpPacket.getPayload() != null ?
                                            originalTcpPacket.getPayload().length() : 0)
                    )
                    .psh(true)
                    .ack(true)
                    .payloadBuilder(new UnknownPacket.Builder().rawData(responseData))
                    .correctChecksumAtBuild(true)
                    .correctLengthAtBuild(true);

            // Построение IP пакета (предположим IPv4)
            IpV4Packet.Builder ipBuilder = new IpV4Packet.Builder();
            ipBuilder.version(IpVersion.IPV4)
                    .protocol(IpNumber.TCP)
                    .srcAddr((Inet4Address) dstAddress)
                    .dstAddr((Inet4Address) srcAddress)
                    .payloadBuilder(tcpBuilder)
                    .correctChecksumAtBuild(true)
                    .correctLengthAtBuild(true)
                    .ttl((byte) 64)
                    .tos(IpV4Rfc791Tos.newInstance((byte) 0))
                    .identification((short) 100);

            Packet responsePacket = ipBuilder.build();
            byte[] rawData = responsePacket.getRawData();

            // Отправляем в TUN-интерфейс
            this.eventLoop.sendToClient(rawData, 0, rawData.length);

        } catch (Exception e) {
            Timber.e(e, "Failed to send HTTP 403 Forbidden response");
        }
    }


    private IpPacket buildIpPacketForResponse(IpPacket.IpHeader originalIpHeader, TcpPacket tcpPacket) {
        if (originalIpHeader instanceof IpV4Packet.IpV4Header) {
            IpV4Packet.Builder ipBuilder = new IpV4Packet.Builder();
            IpV4Packet.IpV4Header ipv4Header = (IpV4Packet.IpV4Header) originalIpHeader;
            byte ttl = 64;
            ipBuilder
                    .version(IpVersion.IPV4)
                    .tos(ipv4Header.getTos())
                    .ttl(ttl) // обычно 64 для ответов
                    .protocol(IpNumber.TCP)
                    .srcAddr(ipv4Header.getDstAddr())
                    .dstAddr(ipv4Header.getSrcAddr())
                    .payloadBuilder(tcpPacket.getBuilder())
                    .correctChecksumAtBuild(true)
                    .correctLengthAtBuild(true);

            return ipBuilder.build();
        } else if (originalIpHeader instanceof IpV6Packet.IpV6Header) {
            IpV6Packet.Builder ip6Builder = new IpV6Packet.Builder();
            IpV6Packet.IpV6Header ipv6Header = (IpV6Packet.IpV6Header) originalIpHeader;
            byte hopLimit = 64;
            ip6Builder
                    .version(IpVersion.IPV6)
                    .nextHeader(IpNumber.TCP)
                    .hopLimit(hopLimit)
                    .srcAddr(ipv6Header.getDstAddr())
                    .dstAddr(ipv6Header.getSrcAddr())
                    .payloadBuilder(tcpPacket.getBuilder())
                    .correctLengthAtBuild(true);

            return ip6Builder.build();
        } else {
            throw new IllegalArgumentException("Unsupported IP version");
        }
    }
}
