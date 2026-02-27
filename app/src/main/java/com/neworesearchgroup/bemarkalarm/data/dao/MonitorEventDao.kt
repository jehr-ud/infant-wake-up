package com.neworesearchgroup.bemarkalarm.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.neworesearchgroup.bemarkalarm.data.model.MonitorEvent

@Dao
interface MonitorEventDao {

    @Insert
    suspend fun insert(event: MonitorEvent)

    @Query("SELECT * FROM monitor_events ORDER BY timestamp DESC")
    suspend fun getAll(): List<MonitorEvent>

    @Query("UPDATE monitor_events SET wasConfirmed = :confirmed WHERE id = :eventId")
    suspend fun updateFeedback(eventId: Long, confirmed: Boolean)
}