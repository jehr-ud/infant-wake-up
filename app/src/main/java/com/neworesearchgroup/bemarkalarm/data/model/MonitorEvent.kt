package com.neworesearchgroup.bemarkalarm.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monitor_events")
data class MonitorEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val timestamp: Long = System.currentTimeMillis(),

    val score: Float,           // score acústico
    val decisionValue: Float,   // EMA o valor final

    val wasConfirmed: Boolean?  // null = sin feedback
)