package com.example.epic.model.source;

import static com.example.epic.db.entity.ListType.ALLOWED;
import static com.example.epic.db.entity.ListType.BLOCKED;
import static com.example.epic.db.entity.ListType.REDIRECTED;
import static com.example.epic.util.Constants.BOGUS_IPV4;
import static com.example.epic.util.Constants.LOCALHOST_HOSTNAME;
import static com.example.epic.util.Constants.LOCALHOST_IPV4;
import static com.example.epic.util.Constants.LOCALHOST_IPV6;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.epic.db.dao.HostListItemDao;
import com.example.epic.db.dao.RuleListItemDao;
import com.example.epic.db.entity.HostListItem;
import com.example.epic.db.entity.HostsSource;
import com.example.epic.db.entity.ListType;
import com.example.epic.db.entity.RuleListItem;
import com.example.epic.db.hosts.SourceHosts;
import com.example.epic.util.RegexUtils;

import java.io.BufferedReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

/**
 * This class is an {@link HostsSource} loader.<br>
 * It parses a source and loads it to database.
 *
 * @author Bruce BUJON (bruce.bujon(at)gmail(dot)com)
 */
class SourceLoader {
    private static final String TAG = "SourceLoader";
    private static final String END_OF_QUEUE_MARKER = "#EndOfQueueMarker";
    private static final int INSERT_BATCH_SIZE = 100;
    private static final String HOSTS_PARSER = "^\\s*([^#\\s]+)\\s+([^#\\s]+).*$";
    static final Pattern HOSTS_PARSER_PATTERN = Pattern.compile(HOSTS_PARSER);

    private final HostsSource source;
    private final Context context;

    SourceLoader(Context context, HostsSource hostsSource) {
        this.source = hostsSource;
        this.context = context;
    }

    void parse(BufferedReader reader, HostListItemDao hostListItemDao, RuleListItemDao ruleListItemDao) {
        // Clear current hosts
        hostListItemDao.clearSourceHosts(this.source.getId());
        ruleListItemDao.clearSourceRules(this.source.getId());
        // Create batch
        int parserCount = SourceHosts.getAllSources().size();
        LinkedBlockingQueue<String> hostsLineQueue = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<HostListItem> hostsListItemQueue = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<RuleListItem> ruleListItemQueue = new LinkedBlockingQueue<>();
        SourceReader sourceReader = new SourceReader(reader, hostsLineQueue, parserCount);
        ItemInserter inserter = new ItemInserter(hostsListItemQueue, hostListItemDao, ruleListItemQueue, ruleListItemDao, parserCount);
        ExecutorService executorService = Executors.newFixedThreadPool(
                parserCount + 2,
                r -> new Thread(r, TAG)
        );
        executorService.execute(sourceReader);
        for (int i = 0; i < parserCount; i++) {
            executorService.execute(new HostListItemParser(this.source, hostsLineQueue, hostsListItemQueue, ruleListItemQueue, context));
        }
        Future<Integer> inserterFuture = executorService.submit(inserter);
        try {
            Integer inserted = inserterFuture.get();
            Log.i("host list items inserted:", inserted.toString());
        } catch (ExecutionException e) {
            Log.w("Failed to parse hosts sources.", e.getLocalizedMessage());
        } catch (InterruptedException e) {
            Log.w("Interrupted while parsing sources.", e.getLocalizedMessage());
            Thread.currentThread().interrupt();
        }
        executorService.shutdown();
    }

    private static class SourceReader implements Runnable {
        private final BufferedReader reader;
        private final BlockingQueue<String> queue;
        private final int parserCount;

        private SourceReader(BufferedReader reader, BlockingQueue<String> queue, int parserCount) {
            this.reader = reader;
            this.queue = queue;
            this.parserCount = parserCount;
        }

        @Override
        public void run() {
            try {
                this.reader.lines().forEach(this.queue::add);
            } catch (Throwable t) {
                Timber.w(t, "Failed to read hosts source.");
            } finally {
                // Send end of queue marker to parsers
                for (int i = 0; i < this.parserCount; i++) {
                    this.queue.add(END_OF_QUEUE_MARKER);
                }
            }
        }
    }

    private static class HostListItemParser implements Runnable {
        private final HostsSource source;
        private final BlockingQueue<String> lineQueue;
        private final BlockingQueue<HostListItem> itemQueue;
        private final BlockingQueue<RuleListItem> ruleItemQueue;
        private final Context context;

        private HostListItemParser(HostsSource source, BlockingQueue<String> lineQueue, BlockingQueue<HostListItem> itemQueue, LinkedBlockingQueue<RuleListItem> ruleListItemQueue, Context context) {
            this.source = source;
            this.lineQueue = lineQueue;
            this.itemQueue = itemQueue;
            this.ruleItemQueue = ruleListItemQueue;
            this.context = context;
        }

        @Override
        public void run() {
            boolean allowedList = this.source.isAllowEnabled();
            boolean endOfSource = false;
            while (!endOfSource) {
                try {
                    String line = this.lineQueue.take();
                    // Check end of queue marker
                    //noinspection StringEquality
                    if (line == END_OF_QUEUE_MARKER) {
                        endOfSource = true;
                        // Send end of queue marker to inserter
                        HostListItem endItem = new HostListItem();
                        RuleListItem endItemRule = RuleListItem.Companion.empty(line);
                        endItem.setHost(line);
                        this.itemQueue.add(endItem);
                        this.ruleItemQueue.add(endItemRule);
                    } // Check comments
                    else if (line.isEmpty() || line.charAt(0) == '#') {
                        Log.d("Skip comment:", line);
                    } else {
                        HostListItem item = allowedList ? parseAllowListItem(line) : parseHostListItem(line);
                        if (item != null && isRedirectionValid(item) && isHostValid(item)) {
                            this.itemQueue.add(item);
                            Log.d("Add line host:", line);
                        }
                        RuleListItem rule = RulesLoader.INSTANCE.parseAbpLine(line, this.source.getSourceBlockType(), this.source.getId());
                        if (rule != null && item == null) {
                            this.ruleItemQueue.add(rule);
                            Log.d("Add line rule:", line);
                        }
                    }
                } catch (InterruptedException e) {
                    Log.w("Interrupted while parsing hosts list item.", e.getLocalizedMessage());
                    endOfSource = true;
                    Thread.currentThread().interrupt();
                }
            }
            if (endOfSource) {
                Intent intent = new Intent("com.example.epic.ACTION_PARSING_COMPLETE");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        }

        private HostListItem parseHostListItem(String line) {
            Matcher matcher = HOSTS_PARSER_PATTERN.matcher(line);
            if (!matcher.matches()) {
                Timber.d("Does not match: %s.", line);
                return null;
            }
            // Check IP address validity or while list entry (if allowed)
            String ip = matcher.group(1);
            String hostname = matcher.group(2);
            assert hostname != null;
            // Skip localhost name
            if (LOCALHOST_HOSTNAME.equals(hostname)) {
                return null;
            }
            // check if ip is 127.0.0.1 or 0.0.0.0
            ListType type;
            if (LOCALHOST_IPV4.equals(ip)
                    || BOGUS_IPV4.equals(ip)
                    || LOCALHOST_IPV6.equals(ip)) {
                type = BLOCKED;
            } else if (this.source.isRedirectEnabled()) {
                type = REDIRECTED;
            } else {
                return null;
            }
            HostListItem item = new HostListItem();
            item.setType(type);
            item.setHost(hostname);
            item.setEnabled(true);
            item.setSourceBlockType(this.source.getSourceBlockType());
            if (type == REDIRECTED) {
                item.setRedirection(ip);
            }
            item.setSourceId(this.source.getId());
            return item;
        }

        private HostListItem parseAllowListItem(String line) {
            // Extract hostname
            int indexOf = line.indexOf('#');
            if (indexOf == 1) {
                line = line.substring(0, indexOf);
            }
            line = line.trim();
            // Create item
            HostListItem item = new HostListItem();
            item.setType(ALLOWED);
            item.setHost(line);
            item.setEnabled(true);
            item.setSourceId(this.source.getId());
            return item;
        }

        private boolean isRedirectionValid(HostListItem item) {
            return item.getType() != REDIRECTED || RegexUtils.isValidIP(item.getRedirection());
        }

        private boolean isHostValid(HostListItem item) {
            String hostname = item.getHost();
            if (item.getType() == BLOCKED) {
                if (hostname.indexOf('?') != -1 || hostname.indexOf('*') != -1) {
                    return false;
                }
                return RegexUtils.isValidHostname(hostname);
            }
            return RegexUtils.isValidWildcardHostname(hostname);
        }
    }

    private static class ItemInserter implements Callable<Integer> {
        private final BlockingQueue<HostListItem> hostListItemQueue;
        private final HostListItemDao hostListItemDao;
        private final BlockingQueue<RuleListItem> ruleListItemBlockingQueue;
        private final RuleListItemDao ruleListItemDao;
        private final int parserCount;

        private ItemInserter(BlockingQueue<HostListItem> itemQueue, HostListItemDao hostListItemDao, LinkedBlockingQueue<RuleListItem> ruleListItemQueue, RuleListItemDao ruleListItemDao, int parserCount) {
            this.hostListItemQueue = itemQueue;
            this.hostListItemDao = hostListItemDao;
            this.ruleListItemBlockingQueue = ruleListItemQueue;
            this.ruleListItemDao = ruleListItemDao;
            this.parserCount = parserCount;
        }

        @Override
        public Integer call() {
            int inserted = 0;
            int workerStopped = 0;
            HostListItem[] batch = new HostListItem[INSERT_BATCH_SIZE];
            RuleListItem[] ruleBatch = new RuleListItem[INSERT_BATCH_SIZE];
            int cacheSize = 0;
            int ruleCacheSize = 0;
            boolean queueEmptied = false;
            boolean ruleQueueEmptied = false;
            while (!queueEmptied) {
                try {
                    HostListItem item = this.hostListItemQueue.take();
                    // Check end of queue marker
                    //noinspection StringEquality
                    if (item.getHost() == END_OF_QUEUE_MARKER) {
                        workerStopped++;
                        if (workerStopped >= this.parserCount) {
                            queueEmptied = true;
                        }
                    } else {
                        batch[cacheSize++] = item;
                        if (cacheSize >= batch.length) {
                            this.hostListItemDao.insert(batch);
                            inserted += cacheSize;
                            cacheSize = 0;
                        }
                    }
                } catch (InterruptedException e) {
                    Log.w("Interrupted while inserted hosts list item.", e.getLocalizedMessage());
                    queueEmptied = true;
//                    Thread.currentThread().interrupt();
                }
            }
            while (!ruleQueueEmptied) {
                try {
                    RuleListItem ruleListItem = this.ruleListItemBlockingQueue.take();
                    // Check end of queue marker
                    //noinspection StringEquality
                    if (ruleListItem.getPattern() == END_OF_QUEUE_MARKER) {
                        workerStopped++;
                        if (workerStopped >= this.parserCount) {
                            ruleQueueEmptied = true;
                        }
                    } else {
                        ruleBatch[ruleCacheSize++] = ruleListItem;
                        if (ruleCacheSize >= ruleBatch.length) {
                            this.ruleListItemDao.insert(ruleBatch);
                            inserted += ruleCacheSize;
                            ruleCacheSize = 0;
                        }
                    }
                } catch (InterruptedException e) {
                    Log.w("Interrupted while inserted hosts list item.", e.getLocalizedMessage());
                    ruleQueueEmptied = true;
//                    Thread.currentThread().interrupt();
                }
            }
            // Flush current batch
            HostListItem[] remaining = new HostListItem[cacheSize];
            System.arraycopy(batch, 0, remaining, 0, remaining.length);
            this.hostListItemDao.insert(remaining);
            RuleListItem[] remainingRule = new RuleListItem[ruleCacheSize];
            System.arraycopy(ruleBatch, 0, remainingRule, 0, remaining.length);
            this.ruleListItemDao.insert(remainingRule);
            inserted += cacheSize;
            inserted += ruleCacheSize;
            // Return number of inserted items
            return inserted;
        }
    }
}
