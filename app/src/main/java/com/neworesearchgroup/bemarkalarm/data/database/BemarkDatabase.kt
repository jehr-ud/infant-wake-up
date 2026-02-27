package com.neworesearchgroup.bemarkalarm.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.neworesearchgroup.bemarkalarm.data.dao.MonitorEventDao
import com.neworesearchgroup.bemarkalarm.data.model.MonitorEvent

@Database(entities = [MonitorEvent::class], version = 1)
abstract class BemarkDatabase : RoomDatabase() {
    abstract fun monitorEventDao(): MonitorEventDao
}