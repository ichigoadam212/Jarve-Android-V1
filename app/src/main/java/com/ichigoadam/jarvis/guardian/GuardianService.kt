package com.ichigoadam.jarvis.guardian

import android.app.Service
import android.content.Intent
import android.os.IBinder

class GuardianService : Service() {
    private val guardian = Guardian()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Android is responsible for process recreation. Guardian provides
        // the orchestration boundary and prevents unbounded restart loops.
        guardian.resetFailures()
        return START_STICKY
    }

    override fun onDestroy() {
        // Keep cleanup local; never attempt to control Android system_server.
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
