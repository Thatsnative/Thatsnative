package com.example.epic.db.dao;

import static androidx.room.OnConflictStrategy.REPLACE;
import static com.example.epic.db.entity.ListType.REDIRECTED;

import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.epic.analityc.AnalyticLogger;
import com.example.epic.db.entity.HostEntry;
import com.example.epic.db.entity.HostListItem;
import com.example.epic.db.entity.ListType;
import com.example.epic.db.entity.SourceBlockType;
import com.example.epic.helper.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import kotlinx.coroutines.flow.Flow;

/**
 * This interface is the DAO for {@link HostEntry} records.
 *
 * @author Bruce BUJON (bruce.bujon(at)gmail(dot)com)
 */
@Dao
public interface HostEntryDao {
    Pattern ANY_CHAR_PATTERN = Pattern.compile("\\*");
    Pattern A_CHAR_PATTERN = Pattern.compile("\\?");

    @Query("DELETE FROM `host_entries`")
    void clear();

    @Query("INSERT OR REPLACE INTO `host_entries` SELECT DISTINCT `host`, `type`, `redirection`, `source_block_type` FROM `hosts_lists` WHERE `type` = 0 AND `enabled` = 1 AND `source_block_type` IN (:sourceBlockTypes)")
    void importBlocked(ArrayList<String> sourceBlockTypes);

    @Query("SELECT host FROM hosts_lists WHERE type = 1 AND enabled = 1")
    List<String> getEnabledAllowedHosts();

    @Query("SELECT host FROM hosts_lists WHERE type = 1 AND enabled = 1")
    Flow<List<String>> getEnabledAllowedHostsFlow();

    @Query("DELETE FROM `host_entries` WHERE `host` LIKE :hostPattern")
    void allowHost(String hostPattern);

    @Query("SELECT * FROM hosts_lists WHERE type = 2 AND enabled = 1 ORDER BY host ASC, source_id DESC")
    List<HostListItem> getEnabledRedirectedHosts();

    @Insert(onConflict = REPLACE)
    void redirectHost(HostEntry redirection);

    /**
     * Synchronize the host entries based on the current hosts lists table records.
     */
    default void sync() {
        clear();
        ArrayList<String> sourceBlockTypes = new ArrayList<String>(10);
        sourceBlockTypes.add(SourceBlockType.ADBLOCK.name());
        if (PreferenceHelper.isSpeedEnabled()) {
            sourceBlockTypes.add(SourceBlockType.SPEED.name());
        }
        if (PreferenceHelper.isPrivacyProtectionEnabled()) {
            sourceBlockTypes.add(SourceBlockType.PRIVACY.name());
        }
        if (PreferenceHelper.isSecurityEnabled()) {
            sourceBlockTypes.add(SourceBlockType.SECURITY.name());
            ;
        }
        if (PreferenceHelper.isCookieProtectionEnabled()) {
            sourceBlockTypes.add(SourceBlockType.COOKIE.name());
        }
        if (PreferenceHelper.isSocialMediaProtectionEnabled()) {
            sourceBlockTypes.add(SourceBlockType.SOCIAL_MEDIA_WIDGETS.name());
        }
        if (PreferenceHelper.isYoutubeProtectionEnabled()) {
            sourceBlockTypes.add(SourceBlockType.YOUTUBE.name());
        }
        AnalyticLogger.INSTANCE.info(sourceBlockTypes.toString());
        importBlocked(sourceBlockTypes);
        for (String allowedHost : getEnabledAllowedHosts()) {
            allowedHost = ANY_CHAR_PATTERN.matcher(allowedHost).replaceAll("%");
            allowedHost = A_CHAR_PATTERN.matcher(allowedHost).replaceAll("_");
            allowHost(allowedHost);
        }
        for (HostListItem redirectedHost : getEnabledRedirectedHosts()) {
            HostEntry entry = new HostEntry();
            entry.setHost(redirectedHost.getHost());
            entry.setType(REDIRECTED);
            entry.setRedirection(redirectedHost.getRedirection());
            redirectHost(entry);
        }
    }

    @Query("SELECT * FROM `host_entries` ORDER BY `host`")
    List<HostEntry> getAll();

    @Query("SELECT `type` FROM `host_entries` WHERE `host` == :host LIMIT 1")
    ListType getTypeOfHost(String host);

    @Query("SELECT IFNULL((SELECT `type` FROM `host_entries` WHERE `host` == :host LIMIT 1), 1)")
    ListType getTypeForHost(String host);

    @Nullable
    @Query("SELECT * FROM `host_entries` WHERE `host` == :host LIMIT 1")
    HostEntry getEntry(String host);
}
