package com.ichigoadam.jarvis.guardian

import android.app.Service
import android.content.Intent
import android.os.IBinder

class GuardianService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // V1 foundation: monitoring/restart orchestration will be added here.
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
