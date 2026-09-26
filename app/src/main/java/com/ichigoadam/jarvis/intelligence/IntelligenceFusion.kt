package com.ichigoadam.jarvis.intelligence

class IntelligenceFusion {
    fun combine(context: SharedIntelligenceContext): String =
        when {
            context.jarvisResult.isNotBlank() && context.fridayResult.isNotBlank() ->
                context.jarvisResult + " " + context.fridayResult
            context.jarvisResult.isNotBlank() -> context.jarvisResult
            else -> context.fridayResult
        }.trim()
}
