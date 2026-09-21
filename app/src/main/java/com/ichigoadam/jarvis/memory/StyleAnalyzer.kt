package com.ichigoadam.jarvis.memory

class StyleAnalyzer {
    fun analyze(samples: List<String>): ReplyStyleProfile {
        val messages = samples
            .flatMap { it.split("\n".toRegex()) }
            .map { it.trim() }
            .filter { it.length in 2..300 }

        if (messages.isEmpty()) return ReplyStyleProfile()

        val avg = messages.map { it.length }.average().toInt().coerceIn(12, 160)
        val starts = messages
            .map { it.split("\s+".toRegex()).take(2).joinToString(" ") }
            .filter { it.isNotBlank() }
            .groupingBy { it.lowercase() }
            .eachCount()
            .entries.sortedByDescending { it.value }
            .take(5).map { it.key }

        val ends = messages
            .map { it.split("\s+".toRegex()).takeLast(2).joinToString(" ") }
            .filter { it.isNotBlank() }
            .groupingBy { it.lowercase() }
            .eachCount()
            .entries.sortedByDescending { it.value }
            .take(5).map { it.key }

        return ReplyStyleProfile(
            averageLength = avg,
            usesQuestions = messages.count { "?" in it || "؟" in it } >= messages.size / 5,
            usesExclamation = messages.count { "!" in it } >= messages.size / 5,
            usesEllipsis = messages.count { "..." in it || "…" in it } >= messages.size / 5,
            usesEgyptianArabic = messages.count {
                listOf("بقولك", "يبني", "بص", "ماشي", "خلاص", "كده", "ليه", "ايه", "عايز").any { w ->
                    it.contains(w, ignoreCase = true)
                }
            } >= messages.size / 10,
            commonOpeners = starts,
            commonClosers = ends
        )
    }
}
