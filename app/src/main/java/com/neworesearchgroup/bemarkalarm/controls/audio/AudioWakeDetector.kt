package com.neworesearchgroup.bemarkalarm.controls.audio

import android.annotation.SuppressLint
import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission

class AudioWakeDetector(
    private val onWakeDetected: () -> Unit
) {

    // --- PIPELINE ---

    private val extractor = FeatureExtractor()
    private val model = AcousticModel()
    private val decision = HumanAwareDecision()

    private val sampleRate = 16000
    private val windowSize = sampleRate  // 1 segundo
    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    private var recorder: AudioRecord? = null
    private var running = false

    @SuppressLint("MissingPermission")
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun start() {
        if (running) return
        if (bufferSize <= 0) return

        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        if (recorder?.state != AudioRecord.STATE_INITIALIZED) {
            recorder?.release()
            recorder = null
            return
        }

        recorder?.startRecording()
        running = true

        Thread {
            val buffer = ShortArray(windowSize)

            while (running) {
                val read = recorder?.read(buffer, 0, buffer.size) ?: 0
                if (read < windowSize) continue

                val shouldAlert = processAudio(buffer)

                if (shouldAlert) {
                    onWakeDetected()
                }
            }
        }.start()
    }

    fun stop() {
        running = false
        recorder?.stop()
        recorder?.release()
        recorder = null
    }

    // --- PIPELINE CORE ---
    private fun processAudio(samples: ShortArray): Boolean {
        val features = extractor.extract(samples)
        val score = model.score(features)
        return decision.update(score)
    }
}