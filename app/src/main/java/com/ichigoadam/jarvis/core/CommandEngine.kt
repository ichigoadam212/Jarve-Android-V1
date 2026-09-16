package com.ichigoadam.jarvis.core

class CommandEngine {
    fun execute(input: String): CommandResult {
        val command = input.trim().lowercase()
        return when {
            command == "jarvis" -> CommandResult(true, "نعم")
            command == "time" || command == "الوقت" ->
                CommandResult(true, java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date()))
            else -> CommandResult(false)
        }
    }
}
