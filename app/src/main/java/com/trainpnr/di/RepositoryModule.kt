package com.trainpnr.di

import com.trainpnr.data.repository.PreferencesRepositoryImpl
import com.trainpnr.data.repository.SavedPnrRepositoryImpl
import com.trainpnr.domain.repository.PreferencesRepository
import com.trainpnr.domain.repository.SavedPnrRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton abstract fun bindSavedPnrRepository(impl: SavedPnrRepositoryImpl): SavedPnrRepository
    @Binds @Singleton abstract fun bindPreferencesRepository(impl: PreferencesRepositoryImpl): PreferencesRepository
}
