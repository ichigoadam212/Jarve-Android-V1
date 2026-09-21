package com.ichigoadam.jarvis.guardian

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Handler
import android.os.Looper
import com.ichigoadam.jarvis.JarvisService

class GuardianService : Service() {
    companion object {
        private const val CHANNEL_ID = "jarvis_guardian"
        private const val NOTIFICATION_ID = 1002
    }

    private lateinit var guardian: Guardian
    private val handler = Handler(Looper.getMainLooper())
    private val monitor = object : Runnable {
        override fun run() {
            if (!JarvisService.isRunning(applicationContext)) {
                guardian.shouldRestart()
            }
            handler.postDelayed(this, 10_000L)
        }
    }

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
        handler.post(monitor)
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
        handler.removeCallbacks(monitor)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
