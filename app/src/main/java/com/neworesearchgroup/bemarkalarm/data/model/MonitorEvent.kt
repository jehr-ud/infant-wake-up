package com.neworesearchgroup.bemarkalarm.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monitor_events")
data class MonitorEvent(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val score: Float,
    val decisionValue: Float,
    val wasConfirmed: Boolean?,

    val timestamp: Long = System.currentTimeMillis()
)