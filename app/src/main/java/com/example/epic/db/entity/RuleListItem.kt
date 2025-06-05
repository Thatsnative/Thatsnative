package com.example.epic.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "rule_lists",
    indices = [Index(value = ["pattern", "source_id"])],
    foreignKeys = [ForeignKey(
        entity = HostsSource::class,
        parentColumns = ["id"],
        childColumns = ["source_id"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )]
)
data class RuleListItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val pattern: String = "",
    val type: ListType = ListType.ALLOWED,
    val enabled: Boolean = true,
    val options: String = "",
    @ColumnInfo(name = "source_id")
    val sourceId: Int = 0,
    @ColumnInfo(name = "source_block_type")
    val sourceBlockType: String = SourceBlockType.ADBLOCK.name
) {
    companion object {
        @JvmStatic
        fun empty(pattern: String) = RuleListItem(pattern = pattern)
    }
}