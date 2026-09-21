package com.ichigoadam.jarvis.server

class LocalServerPolicy {
    fun canSendOffline(): Boolean = false

    fun shouldUseRemote(enabledByUser: Boolean, networkAvailable: Boolean): Boolean =
        enabledByUser && networkAvailable

    fun describe(): String =
        "Remote bridge is optional; local/offline behavior remains the default."
}
