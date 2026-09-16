package com.ichigoadam.jarvis.core

class CommandRouter {
    private val commands = listOf(
        CommandDefinition("time", CommandPolicy.PUBLIC),
        CommandDefinition("الوقت", CommandPolicy.PUBLIC),
        CommandDefinition("secret", CommandPolicy.SECRET_OWNER_ONLY)
    )

    fun policyFor(input: String): CommandPolicy {
        val normalized = input.trim().lowercase()
        return commands.firstOrNull { it.name == normalized }?.policy
            ?: CommandPolicy.PUBLIC
    }
}
