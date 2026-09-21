package com.ichigoadam.jarvis.core

class CommandRouter {
    private val commands = listOf(
        CommandDefinition("time", CommandPolicy.PUBLIC),
        CommandDefinition("الوقت", CommandPolicy.PUBLIC),
        CommandDefinition("status", CommandPolicy.PUBLIC),
        CommandDefinition("الحالة", CommandPolicy.PUBLIC),

        CommandDefinition("read messages", CommandPolicy.PUBLIC),
        CommandDefinition("اقرأ الرسائل", CommandPolicy.PUBLIC),
        CommandDefinition("read screen", CommandPolicy.PUBLIC),
        CommandDefinition("اقرأ الشاشة", CommandPolicy.PUBLIC),
        CommandDefinition("read last message", CommandPolicy.PUBLIC),
        CommandDefinition("اقرأ آخر رسالة", CommandPolicy.PUBLIC),
        CommandDefinition("what app", CommandPolicy.PUBLIC),
        CommandDefinition("ما التطبيق", CommandPolicy.PUBLIC),
        CommandDefinition("add chat to close", CommandPolicy.PUBLIC),
        CommandDefinition("ضيف الشات للكلوز", CommandPolicy.PUBLIC),
        CommandDefinition("ضيف الشات للـكلوز", CommandPolicy.PUBLIC),
        CommandDefinition("remove chat from close", CommandPolicy.PUBLIC),
        CommandDefinition("شيل الشات من الكلوز", CommandPolicy.PUBLIC),
        CommandDefinition("teach jarvis my style", CommandPolicy.PUBLIC),
        CommandDefinition("learn this chat", CommandPolicy.PUBLIC),
        CommandDefinition("اتعلم من الشات", CommandPolicy.PUBLIC),
        CommandDefinition("علم جارفيس أسلوبي", CommandPolicy.PUBLIC),

        CommandDefinition("reply", CommandPolicy.PUBLIC),
        CommandDefinition("رد", CommandPolicy.PUBLIC),
        CommandDefinition("reply to last", CommandPolicy.PUBLIC),
        CommandDefinition("رد على آخر رسالة", CommandPolicy.PUBLIC),

        CommandDefinition("open whatsapp", CommandPolicy.PUBLIC),
        CommandDefinition("افتح واتساب", CommandPolicy.PUBLIC),
        CommandDefinition("open instagram", CommandPolicy.PUBLIC),
        CommandDefinition("افتح انستجرام", CommandPolicy.PUBLIC),
        CommandDefinition("افتح إنستجرام", CommandPolicy.PUBLIC),
        CommandDefinition("open facebook", CommandPolicy.PUBLIC),
        CommandDefinition("افتح فيسبوك", CommandPolicy.PUBLIC),

        CommandDefinition("secret", CommandPolicy.SECRET_OWNER_ONLY),
        CommandDefinition("الوضع السري", CommandPolicy.SECRET_OWNER_ONLY),
        CommandDefinition("silent", CommandPolicy.SECRET_OWNER_ONLY),
        CommandDefinition("صامت", CommandPolicy.SECRET_OWNER_ONLY),
        CommandDefinition("stop", CommandPolicy.OWNER_ONLY),
        CommandDefinition("إيقاف", CommandPolicy.OWNER_ONLY)
    )

    fun policyFor(input: String): CommandPolicy {
        val normalized = input.trim().lowercase()
        return commands.firstOrNull { normalized == it.name }?.policy
            ?: CommandPolicy.PUBLIC
    }
}
