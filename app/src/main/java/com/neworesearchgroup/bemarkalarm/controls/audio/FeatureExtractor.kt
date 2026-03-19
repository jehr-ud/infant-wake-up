package com.neworesearchgroup.bemarkalarm.controls.audio

import com.neworesearchgroup.bemarkalarm.data.model.AudioFeatures

class FeatureExtractor {

    // Sustained medium intensity, without isolated peaks.
    fun computeEnergy(samples: ShortArray): Float {
        var sum = 0.0
        for (s in samples) {
            val v = s.toDouble() / Short.MAX_VALUE
            sum += v * v
        }
        return (sum / samples.size).toFloat()
    }



    // Capture vocalization characteristics.
    fun computeZCR(samples: ShortArray): Float {
        var zeroCrossings = 0
        for (i in 1 until samples.size) {
            if ((samples[i - 1] > 0 && samples[i] < 0) ||
                (samples[i - 1] < 0 && samples[i] > 0)
            ) {
                zeroCrossings++
            }
        }
        return zeroCrossings.toFloat() / samples.size
    }

    fun estimatePitch(samples: ShortArray, sampleRate: Int = 16000): Float {

        val minLag = sampleRate / 600   // ~26
        val maxLag = sampleRate / 80    // ~200

        var bestLag = 0
        var maxCorr = 0.0

        for (lag in minLag..maxLag) {
            var corr = 0.0
            for (i in 0 until samples.size - lag) {
                corr += samples[i].toDouble() * samples[i + lag]
            }
            if (corr > maxCorr) {
                maxCorr = corr
                bestLag = lag
            }
        }

        return if (bestLag > 0) {
            sampleRate.toFloat() / bestLag
        } else {
            0f
        }
    }

    /*
    We use basic autocorrelation
    Key parameters
    Infants: 300–600 Hz
    Adults: < 300 Hz*/

    fun normalizePitch(pitch: Float): Float {
        return when {
            pitch < 100f -> 0f
            pitch > 600f -> 1f
            else -> (pitch - 100f) / 500f
        }
    }

    fun extract(samples: ShortArray): AudioFeatures {

        val energy = computeEnergy(samples)
        val zcr = computeZCR(samples)
        val rawPitch = estimatePitch(samples)
        val pitch = normalizePitch(rawPitch)

        return AudioFeatures(
            energy = energy,
            zcr = zcr,
            pitch = pitch
        )
    }
}

