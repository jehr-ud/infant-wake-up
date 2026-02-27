package com.neworesearchgroup.bemarkalarm.controls.audio

class AcousticModel {

    var wEnergy = 0.4f
    var wZcr = 0.3f
    var wPitch = 0.3f

    fun score(f: AudioFeatures): Float {
        val s =
            wEnergy * f.energy +
                    wZcr * f.zcr +
                    wPitch * f.pitch

        return s.coerceIn(0f, 1f)
    }
}