package com.neworesearchgroup.bemarkalarm.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monitor_events")
data class MonitorEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val timestamp: Long = System.currentTimeMillis(),

    val score: Float,           // score acoustic
    val decisionValue: Float,   // EMA o final value

    val wasConfirmed: Boolean?  // null = sin feedback
)