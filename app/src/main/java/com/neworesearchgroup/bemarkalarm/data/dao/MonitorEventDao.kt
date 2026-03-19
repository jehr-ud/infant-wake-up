package com.neworesearchgroup.bemarkalarm.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.neworesearchgroup.bemarkalarm.data.model.MonitorEvent

@Dao
interface MonitorEventDao {

    @Insert
    suspend fun insert(event: MonitorEvent)

    @Update
    suspend fun update(event: MonitorEvent)

    @Query("SELECT * FROM monitor_events ORDER BY timestamp DESC")
    suspend fun getAll(): List<MonitorEvent>

    @Query("DELETE FROM monitor_events")
    suspend fun clearAll()
}