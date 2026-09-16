package com.ichigoadam.jarvis.guardian

class Guardian {
    private var failures = 0
    private val maxRestarts = 3

    fun shouldRestart(): Boolean {
        if (failures >= maxRestarts) return false
        failures++
        return true
    }

    fun resetFailures() {
        failures = 0
    }
}
