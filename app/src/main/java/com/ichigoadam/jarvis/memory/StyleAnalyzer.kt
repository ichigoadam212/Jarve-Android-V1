package com.ichigoadam.jarvis.memory

class StyleAnalyzer {
    fun analyze(samples: List<String>): ReplyStyleProfile {
        val messages: List<String> = samples
            .flatMap { sample -> sample.split("\\n") }
            .map { line -> line.trim() }
            .filter { line -> line.length in 2..300 }

        if (messages.isEmpty()) return ReplyStyleProfile()

        val averageLength = messages
            .map { message -> message.length }
            .average()
            .toInt()
            .coerceIn(12, 160)

        fun commonParts(takeLast: Boolean): List<String> {
            return messages
                .map { message ->
                    val words = message.split(Regex("\\\\s+"))
                    val selected = if (takeLast) words.takeLast(2) else words.take(2)
                    selected.joinToString(" ")
                }
                .filter { part -> part.isNotBlank() }
                .groupingBy { part -> part.lowercase() }
                .eachCount()
                .entries
                .sortedByDescending { entry -> entry.value }
                .take(5)
                .map { entry -> entry.key }
        }

        val questionCount = messages.count { message -> "?" in message || "؟" in message }
        val exclamationCount = messages.count { message -> "!" in message }
        val ellipsisCount = messages.count { message -> "..." in message || "…" in message }
        val EgyptianWords = listOf("بقولك", "يبني", "بص", "ماشي", "خلاص", "كده", "ليه", "ايه", "عايز")
        val EgyptianCount = messages.count { message ->
            EgyptianWords.any { word -> message.contains(word, ignoreCase = true) }
        }

        return ReplyStyleProfile(
            averageLength = averageLength,
            usesQuestions = questionCount >= (messages.size / 5).coerceAtLeast(1),
            usesExclamation = exclamationCount >= (messages.size / 5).coerceAtLeast(1),
            usesEllipsis = ellipsisCount >= (messages.size / 5).coerceAtLeast(1),
            usesEgyptianArabic = EgyptianCount >= (messages.size / 10).coerceAtLeast(1),
            commonOpeners = commonParts(takeLast = false),
            commonClosers = commonParts(takeLast = true)
        )
    }
}
