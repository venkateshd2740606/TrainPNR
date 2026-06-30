package com.trainpnr.data.repository

import com.trainpnr.data.local.PreferencesDataStore
import com.trainpnr.data.local.database.dao.SavedPnrDao
import com.trainpnr.data.local.database.entity.SavedPnrEntity
import com.trainpnr.domain.model.SavedPnr
import com.trainpnr.domain.model.UserPreferences
import com.trainpnr.domain.repository.PreferencesRepository
import com.trainpnr.domain.repository.SavedPnrRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedPnrRepositoryImpl @Inject constructor(private val dao: SavedPnrDao) : SavedPnrRepository {
    override fun observeSaved(): Flow<List<SavedPnr>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getSaved(pnr: String): SavedPnr? = dao.getByPnr(pnr)?.toDomain()

    override suspend fun save(entry: SavedPnr) = dao.upsert(entry.toEntity())

    override suspend fun delete(pnr: String) = dao.delete(pnr)
}

@Singleton
class PreferencesRepositoryImpl @Inject constructor(private val dataStore: PreferencesDataStore) : PreferencesRepository {
    override fun getUserPreferences(): Flow<UserPreferences> = dataStore.preferencesFlow
    override suspend fun updatePreferences(transform: (UserPreferences) -> UserPreferences) = dataStore.update(transform)
}

private fun SavedPnrEntity.toDomain() = SavedPnr(pnr, nickname, savedAt, lastStatusText)
private fun SavedPnr.toEntity() = SavedPnrEntity(pnr, nickname, savedAt, lastStatusText)
