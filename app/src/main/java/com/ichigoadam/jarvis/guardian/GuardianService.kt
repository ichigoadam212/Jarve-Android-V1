package com.ichigoadam.jarvis.guardian

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder

class GuardianService : Service() {
    companion object {
        private const val CHANNEL_ID = "jarvis_guardian"
        private const val NOTIFICATION_ID = 1002
    }

    private lateinit var guardian: Guardian

    override fun onCreate() {
        super.onCreate()
        guardian = Guardian(applicationContext)

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                "JARVIS Guardian",
                NotificationManager.IMPORTANCE_LOW
            )
        )

        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("JARVIS Guardian")
            .setContentText("Guardian is monitoring JARVIS")
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        guardian.resetFailures()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
