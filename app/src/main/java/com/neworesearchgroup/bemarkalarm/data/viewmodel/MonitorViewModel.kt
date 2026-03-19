package com.neworesearchgroup.bemarkalarm.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neworesearchgroup.bemarkalarm.controls.audio.FeedbackAdapter

import com.neworesearchgroup.bemarkalarm.data.dao.MonitorEventDao
import com.neworesearchgroup.bemarkalarm.data.model.AcousticModel
import com.neworesearchgroup.bemarkalarm.data.model.MonitorEvent
import kotlinx.coroutines.launch

class MonitorViewModel(
    private val dao: MonitorEventDao
) : ViewModel() {

    private val acousticModel = AcousticModel()
    private val feedbackAdapter = FeedbackAdapter(acousticModel)

    fun saveAlert(score: Float, decisionValue: Float) {
        viewModelScope.launch {
            dao.insert(
                MonitorEvent(
                    score = score,
                    decisionValue = decisionValue,
                    wasConfirmed = null
                )
            )
        }
    }

    fun correctFeedback(event: MonitorEvent) {
        viewModelScope.launch {

            dao.update(
                event.copy(
                    wasConfirmed = true
                )
            )

            feedbackAdapter.correctAlert()
        }
    }

    fun falseFeedback(event: MonitorEvent) {
        viewModelScope.launch {

            dao.update(
                event.copy(
                    wasConfirmed = false
                )
            )

            feedbackAdapter.falseAlert()
        }
    }

    fun clearEvents() {
        viewModelScope.launch {
            dao.clearAll()
        }
    }
}
