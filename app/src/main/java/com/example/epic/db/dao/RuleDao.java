package com.example.epic.db.dao;

import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.epic.analityc.AnalyticLogger;
import com.example.epic.db.entity.RuleListItem;
import com.example.epic.db.entity.SourceBlockType;
import com.example.epic.helper.PreferenceHelper;
import com.example.epic.db.entity.RuleEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Dao
public interface RuleDao {
    Pattern ANY_CHAR_PATTERN = Pattern.compile("\\*");
    Pattern A_CHAR_PATTERN = Pattern.compile("\\?");

    @Query("DELETE FROM `rule_entries`")
    void clear();

    @Query("INSERT OR REPLACE INTO `rule_entries` SELECT DISTINCT `pattern`, `type`, `options`, `source_block_type` FROM `rule_lists` WHERE `type` = 0 AND `enabled` = 1 AND `source_block_type` IN (:sourceBlockTypes)")
    void importBlocked(ArrayList<String> sourceBlockTypes);

    @Query("SELECT * FROM `rule_lists` WHERE type = 1 AND enabled = 1")
    List<RuleListItem> getEnabledAllowedRules();

    @Query("DELETE FROM `rule_entries` WHERE `pattern` LIKE :hostPattern")
    void allowRule(String hostPattern);

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
        for (RuleListItem allowRule : getEnabledAllowedRules()) {
            allowRule.copy(
                    allowRule.getId(),
                    ANY_CHAR_PATTERN.matcher(allowRule.getPattern()).replaceAll("%"),
                    allowRule.getType(),
                    allowRule.getEnabled(),
                    allowRule.getOptions(),
                    allowRule.getSourceId(),
                    allowRule.getSourceBlockType()
            );
            allowRule.copy(
                    allowRule.getId(),
                    A_CHAR_PATTERN.matcher(allowRule.getPattern()).replaceAll("_"),
                    allowRule.getType(),
                    allowRule.getEnabled(),
                    allowRule.getOptions(),
                    allowRule.getSourceId(),
                    allowRule.getSourceBlockType()
            );
            allowRule(allowRule.getPattern());
        }
    }

    @Query("SELECT * FROM `rule_entries` ORDER BY `pattern`")
    List<RuleEntity> getAll();

    @Nullable
    @Query("SELECT * FROM rule_entries WHERE :url GLOB pattern AND type = 0  LIMIT 1")
    RuleEntity matchRules(String url);
}
