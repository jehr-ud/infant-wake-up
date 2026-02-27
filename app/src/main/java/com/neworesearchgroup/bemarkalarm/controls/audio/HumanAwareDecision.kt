package com.neworesearchgroup.bemarkalarm.controls.audio

class HumanAwareDecision {

    private var ema = 0f
    private var activeSeconds = 0
    private var cooldown = false

    private val alpha = 0.7f
    private val threshold = 0.6f
    private val persistence = 5

    fun update(score: Float): Boolean {
        if (cooldown) return false

        ema = alpha * score + (1 - alpha) * ema

        if (ema > threshold) {
            activeSeconds++
        } else {
            activeSeconds = 0
        }

        if (activeSeconds >= persistence) {
            cooldown = true
            activeSeconds = 0
            return true
        }
        return false
    }

    fun resetCooldown() {
        cooldown = false
    }
}