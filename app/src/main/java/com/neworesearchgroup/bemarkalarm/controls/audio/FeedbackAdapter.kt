package com.neworesearchgroup.bemarkalarm.controls.audio

import com.neworesearchgroup.bemarkalarm.data.model.AcousticModel

class FeedbackAdapter(
    private val model: AcousticModel
) {

    fun falseAlert() {
        model.adjustForFalseAlert()
    }

    fun correctAlert() {
        model.adjustForCorrectAlert()
    }
}