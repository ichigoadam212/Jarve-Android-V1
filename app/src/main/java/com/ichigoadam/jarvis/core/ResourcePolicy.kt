package com.ichigoadam.jarvis.core

/**
 * Keeps background work bounded so JARVIS does not continuously allocate
 * large buffers, models, or worker pools on the phone.
 */
object ResourcePolicy {
    const val MAX_BACKGROUND_WORKERS = 1
    const val MAX_COMMAND_QUEUE = 8
    const val IDLE_TIMEOUT_MS = 30_000L
}
