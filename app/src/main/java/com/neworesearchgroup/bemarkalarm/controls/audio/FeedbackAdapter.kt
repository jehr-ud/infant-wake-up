package com.neworesearchgroup.bemarkalarm.controls.audio

class FeedbackAdapter(private val model: AcousticModel) {

    fun falseAlert() {
        model.wEnergy -= 0.05f
        model.wPitch += 0.05f
    }

    fun correctAlert() {
        model.wPitch += 0.02f
    }
}