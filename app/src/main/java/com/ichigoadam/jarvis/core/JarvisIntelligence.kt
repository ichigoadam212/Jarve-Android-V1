package com.ichigoadam.jarvis.core

import android.content.Context
import com.ichigoadam.jarvis.friday.FridayModule

/**
 * JARVIS intelligence coordinator.
 * Keeps FRIDAY behind the JARVIS identity instead of exposing a second assistant.
 */
class JarvisIntelligence(context: Context) {
    private val friday = FridayModule(context.applicationContext)

    fun process(command: String): CommandResult {
        val result = friday.analyze(command)
        return if (result.handled) {
            CommandResult(true, result.response)
        } else {
            CommandResult(false)
        }
    }

    fun fridayCapabilities() = friday.capabilities()
}
