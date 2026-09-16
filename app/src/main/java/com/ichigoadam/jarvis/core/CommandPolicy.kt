package com.ichigoadam.jarvis.core

enum class CommandPolicy {
    PUBLIC,
    OWNER_ONLY,
    SECRET_OWNER_ONLY
}

data class CommandDefinition(
    val name: String,
    val policy: CommandPolicy
)
