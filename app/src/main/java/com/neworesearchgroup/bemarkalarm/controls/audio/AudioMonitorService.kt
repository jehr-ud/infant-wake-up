package com.neworesearchgroup.bemarkalarm.controls.audio

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.*
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.neworesearchgroup.bemarkalarm.R
import com.neworesearchgroup.bemarkalarm.data.database.BemarkDatabase
import com.neworesearchgroup.bemarkalarm.data.model.MonitorEvent
import com.neworesearchgroup.bemarkalarm.ui.utils.AlarmPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.sqrt

/* -----------------------------------------------------
   Utils
----------------------------------------------------- */

@RequiresPermission(Manifest.permission.VIBRATE)
fun vibrate(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vm.defaultVibrator.vibrate(
            VibrationEffect.createWaveform(longArrayOf(0, 500, 500), 0)
        )
    } else {
        @Suppress("DEPRECATION")
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        v.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 500, 500), 0))
    }
}

@RequiresPermission(Manifest.permission.VIBRATE)
fun stopVibration(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vm.defaultVibrator.cancel()
    } else {
        @Suppress("DEPRECATION")
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        v.cancel()
    }
}

fun createAlarmChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "bemark_alarm",
            "Bemark Alerts",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
fun showAlarmNotification(context: Context) {
    val notification = NotificationCompat.Builder(context, "bemark_alarm")
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle("Bemark Alert")
        .setContentText("Baby crying detected")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    NotificationManagerCompat.from(context).notify(1001, notification)
}

/* -----------------------------------------------------
   SERVICE
----------------------------------------------------- */

class AudioMonitorService : Service() {

    private lateinit var database: BemarkDatabase
    companion object {
        private const val CHANNEL_ID = "audio_monitor_channel"
        private const val NOTIF_ID = 101
        val isListening = MutableStateFlow(false)
    }

    private lateinit var alarmPlayer: AlarmPlayer
    private var audioRecord: AudioRecord? = null
    private var recordingThread: Thread? = null
    private var running = false

    /* Audio params */
    private val sampleRate = 16000
    private val bufferSize by lazy {
        AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
    }

    /* Cry detection params */
    private val rmsThreshold = 200.0
    private val minCryDurationMs = 60
    private val minZcr = 0.20
    private val maxZcr = 0.45

    private var isPotentialCry = false
    private var cryStartTime = 0L
    private var alarmActive = false

    override fun onCreate() {
        super.onCreate()
        createServiceChannel()
        createAlarmChannel(this)
        alarmPlayer = AlarmPlayer(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        startForeground(NOTIF_ID, buildServiceNotification())

        if (!hasAudioPermission()) {
            Log.e("AudioMonitor", "RECORD_AUDIO permission missing")
            stopSelf()
            return START_NOT_STICKY
        }

        initRecorder()
        startListening()

        return START_STICKY
    }

    private fun saveEvent(score: Float, decisionValue: Float) {

        database = Room.databaseBuilder(
            applicationContext,
            BemarkDatabase::class.java,
            "bemark_db"
        ).build()

        CoroutineScope(Dispatchers.IO).launch {
            database.monitorEventDao().insert(
                MonitorEvent(
                    score = score,
                    decisionValue = decisionValue,
                    wasConfirmed = null
                )
            )
        }
    }

    override fun onDestroy() {
        stopAlarm()
        stopListening()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    /* -----------------------------------------------------
       AUDIO
    ----------------------------------------------------- */

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private fun initRecorder() {
        if (audioRecord != null) return

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
            Log.e("AudioMonitor", "AudioRecord init failed")
            stopSelf()
        }
    }

    private fun startListening() {
        if (running || audioRecord == null) return

        running = true
        audioRecord?.startRecording()
        isListening.value = true

        recordingThread = Thread {
            val buffer = ShortArray(bufferSize)

            while (running) {
                val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (read <= 0) continue

                val rms = calculateRms(buffer, read)
                val zcr = calculateZcr(buffer, read)

                Log.d("AudioMonitor", "RMS=$rms ZCR=$zcr")

                val score = calculateScore(rms, zcr)

                if (detectCry(rms, zcr)) {
                    Log.d("AudioMonitor", "👶 CRY DETECTED")
                    onWakeDetected(
                        score = score,
                        decisionValue = score
                    )
                    resetCryDetection()
                }
            }
        }.apply { start() }
    }

    private fun calculateScore(rms: Double, zcr: Double): Float {

        val rmsNorm = (rms / 1000.0).coerceIn(0.0, 1.0)
        val zcrNorm = zcr.coerceIn(0.0, 1.0)

        return (0.7 * rmsNorm + 0.3 * zcrNorm).toFloat()
    }

    private fun stopListening() {
        running = false
        recordingThread?.interrupt()
        recordingThread = null

        audioRecord?.let {
            if (it.recordingState == AudioRecord.RECORDSTATE_RECORDING) it.stop()
            it.release()
        }
        audioRecord = null
        isListening.value = false
    }

    /* -----------------------------------------------------
       SIGNAL PROCESSING
    ----------------------------------------------------- */

    private fun calculateRms(buffer: ShortArray, read: Int): Double {
        var sum = 0.0
        for (i in 0 until read) {
            val s = buffer[i].toDouble()
            sum += s * s
        }
        return sqrt(sum / read)
    }

    private fun calculateZcr(buffer: ShortArray, read: Int): Double {
        var crossings = 0
        for (i in 1 until read) {
            if ((buffer[i - 1] > 0 && buffer[i] < 0) ||
                (buffer[i - 1] < 0 && buffer[i] > 0)
            ) crossings++
        }
        return crossings.toDouble() / read
    }

    private fun detectCry(rms: Double, zcr: Double): Boolean {
        val now = System.currentTimeMillis()
        val cryLike = rms > rmsThreshold && zcr in minZcr..maxZcr

        if (cryLike) {
            if (!isPotentialCry) {
                isPotentialCry = true
                cryStartTime = now
            }
        } else {
            resetCryDetection()
        }

        return isPotentialCry && (now - cryStartTime) >= minCryDurationMs
    }

    private fun resetCryDetection() {
        isPotentialCry = false
        cryStartTime = 0L
    }

    /* -----------------------------------------------------
       ALARM
    ----------------------------------------------------- */

    @RequiresPermission(Manifest.permission.VIBRATE)
    private fun onWakeDetected(score: Float, decisionValue: Float) {
        if (alarmActive) {
            return
        }

        alarmActive = true

        saveEvent(score, decisionValue)

        alarmPlayer.play()
        vibrate(this)
        showAlarmNotification(this)
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    private fun stopAlarm() {
        alarmPlayer.stop()
        stopVibration(this)
        NotificationManagerCompat.from(this).cancel(1001)
        alarmActive = false
    }

    /* -----------------------------------------------------
       NOTIFICATIONS
    ----------------------------------------------------- */

    private fun buildServiceNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Bemark monitoring")
            .setContentText("Listening for baby crying")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createServiceChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Audio Monitoring",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    /* -----------------------------------------------------
       PERMISSIONS
    ----------------------------------------------------- */

    private fun hasAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }
}
