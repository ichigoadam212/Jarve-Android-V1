package com.ichigoadam.jarvis

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder

class JarvisService : Service() {
    companion object {
        private const val CHANNEL_ID = "jarvis_service"
        private const val NOTIFICATION_ID = 1001
        fun start(context: Context) {
            context.startForegroundService(Intent(context, JarvisService::class.java))
        }
    }
    override fun onCreate() {
        super.onCreate()
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "JARVIS", NotificationManager.IMPORTANCE_LOW)
        )
        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("JARVIS")
            .setContentText("JARVIS Android V1 is running")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_NOT_STICKY
    override fun onBind(intent: Intent?): IBinder? = null
}
