package com.ichigoadam.jarvis.core

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommandEngine {
    fun execute(input: String): CommandResult {
        val command = input.trim().lowercase(Locale.getDefault())

        return when {
            command == "time" || command == "الوقت" ->
                CommandResult(
                    true,
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                )

            command == "status" || command == "الحالة" ->
                CommandResult(true, "JARVIS متصل")

            command == "stop" || command == "إيقاف" ->
                CommandResult(true, "تم إيقاف الاستماع")

            else -> CommandResult(false)
        }
    }
}
