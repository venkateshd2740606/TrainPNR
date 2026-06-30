package com.trainpnr.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.trainpnr.data.local.database.entity.SavedPnrEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedPnrDao {
    @Query("SELECT * FROM saved_pnr ORDER BY savedAt DESC")
    fun observeAll(): Flow<List<SavedPnrEntity>>

    @Query("SELECT * FROM saved_pnr WHERE pnr = :pnr LIMIT 1")
    suspend fun getByPnr(pnr: String): SavedPnrEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SavedPnrEntity)

    @Query("DELETE FROM saved_pnr WHERE pnr = :pnr")
    suspend fun delete(pnr: String)
}
