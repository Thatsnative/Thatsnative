package com.example.epic.db.hosts

import com.example.epic.db.entity.HostsSource
import com.example.epic.db.entity.SourceBlockType

object SourceHosts {
    private val adBlockHosts = mapOf(
        "adaway" to "https://adaway.org/hosts.txt",
        "iVOID" to "https://iosprivacy.com/ivoid/raw/iVOID.hosts",
        "StevenBlackUnifiedHosts" to "https://raw.githubusercontent.com/StevenBlack/hosts/master/hosts",
        "block-everything" to "https://raw.githubusercontent.com/RedDragonWebDesign/block-everything/refs/heads/master/block-everything.txt",
    )
    private val speedHosts = mapOf(
        "EasyList" to "https://easylist-downloads.adblockplus.org/easylist.txt",
        "liste_fr+EasyList" to "https://easylist-downloads.adblockplus.org/liste_fr+easylist.txt",
        "PeterLowesBlocklist" to "https://pgl.yoyo.org/adservers/serverlist.php?hostformat=hosts&showintro=0&mimetype=plaintext",
    )
    private val privacyHosts = mapOf(
        "EasyPrivacy" to "https://easylist-downloads.adblockplus.org/easyprivacy.txt",
        "AntiFacebook" to "https://fanboy.co.nz/fanboy-antifacebook.txt",
        "FanboyUltimate" to "https://fanboy.co.nz/r/fanboy-ultimate.txt",
        "SomeoneWhoCares" to "https://someonewhocares.org/hosts/hosts",
        "Add.2o7Net" to "https://winhelp2002.mvps.org/hosts.txt"
    )
    private val securityHosts = mapOf(
        "ultimate-security-filter" to "https://filters.adavoid.org/ultimate-security-filter.txt",
        "NoCoin" to "https://raw.githubusercontent.com/hoshsadiq/adblock-nocoin-list/refs/heads/master/nocoin.txt",
        "NoCoin" to "https://raw.githubusercontent.com/hoshsadiq/adblock-nocoin-list/master/hosts.txt",
        "Notracking Malware" to "https://raw.githubusercontent.com/notracking/hosts-blocklists/master/hostnames.txt",
        "Spam404" to "https://raw.githubusercontent.com/Spam404/lists/refs/heads/master/adblock-list.txt",
        "BlackSpam404" to "https://raw.githubusercontent.com/Spam404/lists/refs/heads/master/main-blacklist.txt",
        "KADhosts" to "https://raw.githubusercontent.com/FiltersHeroes/KADhosts/master/KADhosts.txt",
    )
    private val annoyancesHosts = mapOf(
        "IDoNotCareAboutCookies" to "https://www.i-dont-care-about-cookies.eu/abp/",
        "fanboyCookieMonster" to "https://secure.fanboy.co.nz/fanboy-cookiemonster.txt",
        "fanboyAnnoyance" to "https://secure.fanboy.co.nz/fanboy-annoyance.txt",
        "StevenBlackUnifiedHosts" to "https://raw.githubusercontent.com/StevenBlack/hosts/master/hosts"
    )
    private val socialMediaWidgetsHosts = mapOf(
        "fanboy-social" to "https://easylist-downloads.adblockplus.org/fanboy-social.txt",
//        "AdGuardFilter1" to "https://filters.adtidy.org/extension/chromium/filters/1.txt",
//        "AdGuardFilter2" to "https://filters.adtidy.org/extension/chromium/filters/2.txt",
//        "AdGuardFilter3" to "https://filters.adtidy.org/extension/chromium/filters/3.txt",
        "AdGuardFilter4" to "https://filters.adtidy.org/extension/chromium/filters/4.txt",
    )

    private val youtubeHosts = mapOf(
        "pihole-youtube-adblock" to "https://raw.githubusercontent.com/LaurentFough/pihole-youtube-adblock/master/pihole-youtube-adblock.txt",
        "pihole-google-adblock" to "https://raw.githubusercontent.com/nickspaargaren/pihole-google/master/categories/doubleclick.txt",
        "Ewpratten" to "https://raw.githubusercontent.com/Ewpratten/youtube_ad_blocklist/master/blocklist.txt",
        "jerryn70" to "https://raw.githubusercontent.com/jerryn70/GoodbyeAds/master/Core/YouTube/YoutubeAdblock-4.txt",
        "The_Quantum_Youtube-Ads-List" to "https://gitlab.com/The_Quantum_Alpha/the-quantum-ad-list/-/raw/master/Individual%20lists/The_Quantum_Youtube-Ads-List.txt",
        "ublockStaticCustomFilters" to "https://gist.githubusercontent.com/aggarwalsushant/9461f3b09d6a08c9f03f74071a066996/raw/536886701dddb4c08edbbbfea2cadcadddb00064/ublockStaticCustomFilters.txt",
        "yt-neuter-hyper" to "https://raw.githubusercontent.com/mchangrh/yt-neuter/refs/heads/main/filters/hyper.txt",
        "yt-neuter-misc" to "https://raw.githubusercontent.com/mchangrh/yt-neuter/refs/heads/main/filters/misc.txt",
        "yt-neuter-nolive" to "https://raw.githubusercontent.com/mchangrh/yt-neuter/refs/heads/main/filters/nolive.txt",
        "yt-neuter-nomusic" to "https://raw.githubusercontent.com/mchangrh/yt-neuter/refs/heads/main/filters/nomusic.txt",
        "yt-neuter-noshorts" to "https://raw.githubusercontent.com/mchangrh/yt-neuter/refs/heads/main/filters/noshorts.txt",
    )

    @JvmStatic
    fun getAdBlockHosts() = adBlockHosts.map {
        HostsSource().apply {
            label = it.key
            url = it.value
            sourceBlockType = SourceBlockType.ADBLOCK.name
        }
    }

    @JvmStatic
    fun getSpeedHosts() = speedHosts.map {
        HostsSource().apply {
            label = it.key
            url = it.value
            sourceBlockType = SourceBlockType.SPEED.name
        }
    }

    @JvmStatic
    fun getPrivacyHosts() = privacyHosts.map {
        HostsSource().apply {
            label = it.key
            url = it.value
            sourceBlockType = SourceBlockType.PRIVACY.name
        }
    }

    @JvmStatic
    fun getSecurityHosts() = securityHosts.map {
        HostsSource().apply {
            label = it.key
            url = it.value
            sourceBlockType = SourceBlockType.SECURITY.name
        }
    }

    @JvmStatic
    fun getAnnoyancesHosts() = annoyancesHosts.map {
        HostsSource().apply {
            label = it.key
            url = it.value
            sourceBlockType = SourceBlockType.COOKIE.name
        }
    }

    @JvmStatic
    fun getSocialMediaWidgetsHosts() = socialMediaWidgetsHosts.map {
        HostsSource().apply {
            label = it.key
            url = it.value
            sourceBlockType = SourceBlockType.SOCIAL_MEDIA_WIDGETS.name
        }
    }

    @JvmStatic
    fun getYoutubeHosts() = youtubeHosts.map {
        HostsSource().apply {
            label = it.key
            url = it.value
            sourceBlockType = SourceBlockType.YOUTUBE.name
        }
    }

    @JvmStatic
    fun getAllSources() = emptyList<HostsSource>()
//        getAdBlockHosts() + getSpeedHosts() + getPrivacyHosts() + getSecurityHosts() + getAnnoyancesHosts() + getSocialMediaWidgetsHosts()
//    + getYoutubeHosts()
}