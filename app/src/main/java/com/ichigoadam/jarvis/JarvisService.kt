package com.ichigoadam.jarvis

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.ichigoadam.jarvis.core.JarvisController

class JarvisService : Service() {
    companion object {
        private const val CHANNEL_ID = "jarvis_service"
        private const val NOTIFICATION_ID = 1001
        private const val PREFS = "jarvis_state"
        private const val KEY_RUNNING = "running"

        fun start(context: Context) {
            context.startForegroundService(Intent(context, JarvisService::class.java))
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, JarvisService::class.java))
        }

        fun isRunning(context: Context): Boolean =
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getBoolean(KEY_RUNNING, false)

        private fun setRunning(context: Context, running: Boolean) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_RUNNING, running).apply()
        }
    }

    private var controller: JarvisController? = null

    override fun onCreate() {
        super.onCreate()
        setRunning(this, true)

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
        controller = JarvisController(this)
        controller?.listen()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onDestroy() {
        controller?.destroy()
        controller = null
        setRunning(this, false)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
