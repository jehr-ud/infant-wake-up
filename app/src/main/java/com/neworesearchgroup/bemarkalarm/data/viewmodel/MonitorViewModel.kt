package com.neworesearchgroup.bemarkalarm.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.neworesearchgroup.bemarkalarm.data.dao.MonitorEventDao
import com.neworesearchgroup.bemarkalarm.data.model.MonitorEvent
import kotlinx.coroutines.launch

class MonitorViewModel(
    private val dao: MonitorEventDao
) : ViewModel() {

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

    fun updateFeedback(eventId: Long, confirmed: Boolean) {
        viewModelScope.launch {
            dao.updateFeedback(eventId, confirmed)
        }
    }
}
