package com.trainpnr.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_pnr")
data class SavedPnrEntity(
    @PrimaryKey val pnr: String,
    val nickname: String,
    val savedAt: Long,
    val lastStatusText: String? = null
)
