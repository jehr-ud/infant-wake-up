package com.neworesearchgroup.bemarkalarm.ui.utils

import android.content.Context
import android.media.MediaPlayer
import com.neworesearchgroup.bemarkalarm.R

class AlarmPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    fun play() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.alarm)
            mediaPlayer?.isLooping = true
        }
        mediaPlayer?.start()
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
