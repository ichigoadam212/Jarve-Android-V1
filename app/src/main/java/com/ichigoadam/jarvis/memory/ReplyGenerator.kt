package com.ichigoadam.jarvis.memory

class ReplyGenerator(
    private val analyzer: StyleAnalyzer = StyleAnalyzer()
) {
    fun generate(incoming: String, samples: List<String>): String {
        val text = incoming.trim()
        if (text.isBlank()) return ""

        val profile = analyzer.analyze(samples)
        val base = when {
            looksLikeQuestion(text) -> questionReply(text, profile)
            isGreeting(text) -> greetingReply(profile)
            looksLikeThanks(text) -> thanksReply(profile)
            else -> neutralReply(text, profile)
        }

        return fitStyle(base, profile)
    }

    private fun questionReply(text: String, profile: ReplyStyleProfile): String {
        val lower = text.lowercase()
        return when {
            lower.contains("عامل") || lower.contains("اخبار") ->
                "تمام الحمد لله، وانت؟"
            lower.contains("فين") || lower.contains("اين") ->
                "موجود اهو، قول"
            lower.contains("ليه") || lower.contains("لماذا") ->
                "عشان كده ببساطة"
            else -> if (profile.usesQuestions) "اه فهمتك، تقصد ايه بالظبط؟" else "اه فهمتك"
        }
    }

    private fun greetingReply(profile: ReplyStyleProfile): String =
        if (profile.usesEgyptianArabic) "اهلا، عامل ايه؟" else "أهلاً، عامل إيه؟"

    private fun thanksReply(profile: ReplyStyleProfile): String =
        if (profile.usesExclamation) "العفو!" else "العفو"

    private fun neutralReply(text: String, profile: ReplyStyleProfile): String {
        val prefix = if (profile.usesEgyptianArabic) "تمام، فهمتك" else "تمام، فهمتك."
        return if (text.length < 35) prefix else "تمام، فهمت قصدك"
    }

    private fun looksLikeQuestion(text: String): Boolean =
        "?" in text || "؟" in text ||
            listOf("ليه", "ازاي", "إزاي", "ايه", "إيه", "فين", "متى", "هل").any {
                text.startsWith(it)
            }

    private fun isGreeting(text: String): Boolean =
        listOf("اهلا", "أهلا", "السلام عليكم", "هاي", "hello", "hi").any {
            text.trim().startsWith(it, ignoreCase = true)
        }

    private fun looksLikeThanks(text: String): Boolean =
        listOf("شكرا", "شكرًا", "متشكر", "thanks").any {
            text.contains(it, ignoreCase = true)
        }

    private fun fitStyle(reply: String, profile: ReplyStyleProfile): String {
        var result = reply.trim()
        if (profile.usesEllipsis && !result.contains(".")) result += "..."
        if (profile.usesExclamation && !result.endsWith("!")) result += "!"
        return result.take(profile.averageLength.coerceAtLeast(20).coerceAtMost(180))
    }
}
