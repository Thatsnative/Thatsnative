package com.example.epic.model.source

import com.example.epic.common.EMPTY_STRING
import com.example.epic.db.entity.ListType
import com.example.epic.db.entity.RuleListItem

object RulesLoader {
    fun parseAbpLine(line: String, sourceBlockType: String, sourceId: Int): RuleListItem? {
        if (line.startsWith("!") || line.isBlank()) return null

        val allowed = line.startsWith("@@")
        val cleanedLine = if (allowed) line.removePrefix("@@") else line

        val parts = cleanedLine.split('$', limit = 2)
        val rawPattern = parts[0].trim()
        val options = parts.getOrNull(1)?.trim()

        // 💡 Новый: поддержка $cookie
        if (options?.contains("cookie") == true) {
            val cookiePattern = rawPattern
                .replace("##", EMPTY_STRING)
                .replace("#?#", EMPTY_STRING)
                .replace("\$document", EMPTY_STRING)
            val cookieName = extractCookieName(options)
            return RuleListItem(
                pattern = cookiePattern,
                type = ListType.BLOCKED,
                enabled = true,
                sourceId = sourceId,
                sourceBlockType = sourceBlockType,
                options = cookieName ?: "cookie"
            )
        }

        if (line.contains("##") || line.contains("#?#") || line.contains("\$document")) return null

        if (rawPattern.isEmpty()) return null
        if (!rawPattern.contains("/") && !rawPattern.contains(".")) return null

        var globPattern = rawPattern
            .replace("||", "*")
            .replace("^", "*")
            .replace("|", "")
            .replace("?", "*")
            .replace(".*", "*")
            .replace(Regex("\\*+"), "*")
            .replace(Regex("(?<!:)//+"), "/")

        if (!globPattern.startsWith("*")) globPattern = "*$globPattern"
        if (!globPattern.endsWith("*")) globPattern = "$globPattern*"

        return RuleListItem(
            pattern = globPattern,
            type = if (allowed) ListType.ALLOWED else ListType.BLOCKED,
            enabled = true,
            sourceId = sourceId,
            sourceBlockType = sourceBlockType,
            options = options.orEmpty(),
        )
    }

    // 🧠 Вспомогательная функция: достать имя куки из фильтра $cookie=name
    private fun extractCookieName(options: String): String? {
        val match = Regex("cookie=([^,\\s]+)").find(options)
        return match?.groupValues?.get(1)
    }
}
