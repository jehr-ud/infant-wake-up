package com.neworesearchgroup.bemarkalarm.controls.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationUtils {

    const val CHANNEL_ID = "bemark_monitor"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Bemark Monitoring",
                NotificationManager.IMPORTANCE_LOW
            )

            val manager =
                context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
