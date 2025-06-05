package com.example.epic.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "rule_entries",
    primaryKeys = ["pattern"]
)
data class RuleEntity(
    val pattern: String,                 // Пример: "||example.com/path"
    val type: ListType,
    @ColumnInfo(name = "source_block_type")
    val sourceBlockType: String,
    val options: String? = null         // Пример: "script", "third-party"
)