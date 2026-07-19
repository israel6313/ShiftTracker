package com.shifttracker.app.di

import android.content.Context
import androidx.room.Room
import com.shifttracker.app.data.db.AppDatabase
import com.shifttracker.app.data.db.dao.JobDao
import com.shifttracker.app.data.db.dao.ShiftDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideJobDao(db: AppDatabase): JobDao = db.jobDao()
    @Provides fun provideShiftDao(db: AppDatabase): ShiftDao = db.shiftDao()
}
