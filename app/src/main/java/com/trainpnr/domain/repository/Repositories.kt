package com.trainpnr.domain.repository

import com.trainpnr.domain.model.SavedPnr
import com.trainpnr.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface SavedPnrRepository {
    fun observeSaved(): Flow<List<SavedPnr>>
    suspend fun getSaved(pnr: String): SavedPnr?
    suspend fun save(entry: SavedPnr)
    suspend fun delete(pnr: String)
}

interface PreferencesRepository {
    fun getUserPreferences(): Flow<UserPreferences>
    suspend fun updatePreferences(transform: (UserPreferences) -> UserPreferences)
}
