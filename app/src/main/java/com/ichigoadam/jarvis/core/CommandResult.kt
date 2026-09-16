package com.ichigoadam.jarvis.core

data class CommandResult(
    val handled: Boolean,
    val response: String = ""
)
