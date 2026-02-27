package com.neworesearchgroup.bemarkalarm.controls.audio

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.neworesearchgroup.bemarkalarm.R
import com.neworesearchgroup.bemarkalarm.controls.notification.NotificationUtils

object AlarmManagerHelper {

    fun triggerAlarm(context: Context) {

        val vibrator =
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(
                    longArrayOf(0, 500, 500),
                    0
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(1000)
        }

        val notification = NotificationCompat.Builder(
            context,
            NotificationUtils.CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Bemark Alert")
            .setContentText("Baby might be awake")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(context)
            .notify(1002, notification)
    }
}
