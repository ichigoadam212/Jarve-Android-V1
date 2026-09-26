package com.ichigoadam.jarvis.intelligence

class AssistantModelRouter {
    fun route(command: String): ModelCommand {
        val normalized = command.trim().lowercase()
        return when {
            normalized.startsWith("friday ") || normalized.startsWith("فرايداي ") ||
                normalized == "friday" || normalized == "فرايداي" ->
                ModelCommand(AssistantModel.FRIDAY, command)
            normalized.startsWith("jarvis ") || normalized.startsWith("جارفيس ") ->
                ModelCommand(AssistantModel.JARVIS, command)
            else -> ModelCommand(AssistantModel.JARVIS, command)
        }
    }
}
