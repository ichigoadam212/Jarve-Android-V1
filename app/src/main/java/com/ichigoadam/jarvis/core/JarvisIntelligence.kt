package com.ichigoadam.jarvis.core

import android.content.Context
import com.ichigoadam.jarvis.friday.FridayModule
import com.ichigoadam.jarvis.intelligence.AssistantModel
import com.ichigoadam.jarvis.intelligence.AssistantModelRouter
import com.ichigoadam.jarvis.intelligence.IntelligenceFusion
import com.ichigoadam.jarvis.intelligence.SharedIntelligenceContext

class JarvisIntelligence(context: Context) {
    private val friday = FridayModule(context.applicationContext)
    private val router = AssistantModelRouter()
    private val fusion = IntelligenceFusion()

    fun process(command: String): CommandResult {
        val route = router.route(command)

        return when (route.model) {
            AssistantModel.FRIDAY -> {
                val result = friday.analyze(stripPrefix(route.command))
                if (result.handled) CommandResult(true, result.response)
                else CommandResult(false)
            }

            AssistantModel.JARVIS -> CommandResult(false)
        }
    }

    /**
     * Shared reasoning entry point: both model outputs can be combined,
     * while permissions and identities remain separate.
     */
    fun fuse(jarvisResult: String, fridayResult: String, command: String): String =
        fusion.combine(
            SharedIntelligenceContext(
                command = command,
                jarvisResult = jarvisResult,
                fridayResult = fridayResult
            )
        )

    fun fridayCapabilities() = friday.capabilities()

    private fun stripPrefix(command: String): String {
        val normalized = command.trim()
        return when {
            normalized.startsWith("friday ", true) -> normalized.substring(7).trim()
            normalized.startsWith("فرايداي ") -> normalized.substring(8).trim()
            normalized.equals("friday", true) || normalized == "فرايداي" -> ""
            else -> normalized
        }
    }
}
