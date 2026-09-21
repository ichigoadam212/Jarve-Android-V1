package com.ichigoadam.jarvis.core

class CommandRouter {
    private val commands = listOf(
        CommandDefinition("time", CommandPolicy.PUBLIC),
        CommandDefinition("الوقت", CommandPolicy.PUBLIC),
        CommandDefinition("status", CommandPolicy.PUBLIC),
        CommandDefinition("الحالة", CommandPolicy.PUBLIC),
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
