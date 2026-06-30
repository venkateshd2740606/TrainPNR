package com.trainpnr.di

import android.content.Context
import androidx.room.Room
import com.trainpnr.data.local.database.TrainPNRDatabase
import com.trainpnr.data.local.database.dao.SavedPnrDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TrainPNRDatabase =
        Room.databaseBuilder(context, TrainPNRDatabase::class.java, "trainpnr.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideSavedPnrDao(db: TrainPNRDatabase): SavedPnrDao = db.savedPnrDao()
}
