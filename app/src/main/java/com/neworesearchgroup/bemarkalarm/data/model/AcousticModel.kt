package com.neworesearchgroup.bemarkalarm.data.model

class AcousticModel(
    private var weights: AcousticWeights = AcousticWeights()
) {

    fun score(features: AudioFeatures): Float {
        val rawScore =
            (weights.energy * features.energy) +
                    (weights.zcr * features.zcr) +
                    (weights.pitch * features.pitch)

        return rawScore.coerceIn(0f, 1f)
    }

    fun adjustForFalseAlert() {
        weights = weights.copy(
            energy = (weights.energy - 0.05f).coerceAtLeast(0f),
            pitch = (weights.pitch + 0.05f).coerceAtMost(1f)
        )
    }

    fun adjustForCorrectAlert() {
        weights = weights.copy(
            pitch = (weights.pitch + 0.02f).coerceAtMost(1f)
        )
    }

    fun getWeights(): AcousticWeights = weights
}