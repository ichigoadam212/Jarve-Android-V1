package com.ichigoadam.jarvis

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class JarvisBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        if (JarvisService.wasIntentionalStop(context)) return

        // Android 14/15 restricts microphone foreground-service startup from BOOT_COMPLETED.
        // Prepare a user-visible launch notification instead of attempting a forbidden start.
        val manager = context.getSystemService(NotificationManager::class.java)
        val channelId = "jarvis_boot"
        manager.createNotificationChannel(
            NotificationChannel(
                channelId,
                "JARVIS startup",
                NotificationManager.IMPORTANCE_LOW
            )
        )

        val launchIntent = Intent(context, MainActivity::class.java)
        val pending = PendingIntent.getActivity(
            context,
            2001,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = android.app.Notification.Builder(context, channelId)
            .setContentTitle("JARVIS")
            .setContentText("JARVIS is ready to start")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()

        manager.notify(2001, notification)
    }
}
