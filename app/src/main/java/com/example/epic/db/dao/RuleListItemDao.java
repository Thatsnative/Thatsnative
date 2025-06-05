package com.example.epic.db.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.lifecycle.LiveData;
import androidx.paging.PagingSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.example.epic.db.entity.RuleListItem;

import java.util.List;
import java.util.Optional;

import kotlinx.coroutines.flow.Flow;

@Dao
public interface RuleListItemDao {
    @Insert(onConflict = REPLACE)
    void insert(RuleListItem... item);

    @Insert(onConflict = REPLACE)
    void insert(List<RuleListItem> items);

    @Update
    void update(RuleListItem item);

    @Delete
    void delete(RuleListItem item);

    @Query("SELECT * FROM rule_lists WHERE type = :type AND pattern LIKE :query AND ((:includeSources == 0 AND source_id == 1) || (:includeSources == 1)) GROUP BY pattern ORDER BY pattern ASC")
    PagingSource<Integer, RuleListItem> loadList(int type, boolean includeSources, String query);

    @Query("SELECT * FROM rule_lists ORDER BY pattern ASC")
    List<RuleListItem> getAll();

    @Query("SELECT * FROM rule_lists ORDER BY pattern ASC")
    PagingSource<Integer, RuleListItem> getAllPaged();

    @Query("SELECT * FROM rule_lists WHERE source_id = 1")
    List<RuleListItem> getUserList();

    @Query("SELECT id FROM rule_lists WHERE pattern = :rule AND source_id = 1 LIMIT 1")
    Optional<Integer> getRuleId(String rule);

    @Query("SELECT COUNT(DISTINCT pattern) FROM rule_lists WHERE type = 0 AND enabled = 1")
    LiveData<Integer> getBlockedRuleCount();

    @Query("SELECT COUNT(DISTINCT pattern) FROM rule_lists WHERE type = 0 AND enabled = 1")
    Flow<Integer> getBlockedRuleCountFlow();

    @Query("SELECT COUNT(DISTINCT pattern) FROM rule_lists WHERE type = 1 AND enabled = 1")
    LiveData<Integer> getAllowedRuleCount();

    @Query("SELECT COUNT(DISTINCT pattern) FROM rule_lists WHERE type = 2 AND enabled = 1")
    LiveData<Integer> getRedirectRuleCount();

    @Query("DELETE FROM rule_lists WHERE source_id = :sourceId")
    void clearSourceRules(int sourceId);
}
