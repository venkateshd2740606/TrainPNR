package com.trainpnr.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.trainpnr.data.local.database.dao.SavedPnrDao
import com.trainpnr.data.local.database.entity.SavedPnrEntity

@Database(entities = [SavedPnrEntity::class], version = 1, exportSchema = false)
abstract class TrainPNRDatabase : RoomDatabase() {
    abstract fun savedPnrDao(): SavedPnrDao
}
