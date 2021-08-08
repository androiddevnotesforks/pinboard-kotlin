package com.fibelatti.pinboard.core.di.modules

import android.app.Application
import androidx.room.Room
import com.fibelatti.pinboard.core.persistence.database.AppDatabase
import com.fibelatti.pinboard.core.persistence.database.DATABASE_NAME
import com.fibelatti.pinboard.core.persistence.database.getAllMigrations
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun appDatabase(application: Application): AppDatabase =
        Room.databaseBuilder(application, AppDatabase::class.java, DATABASE_NAME)
            .addMigrations(*getAllMigrations())
            .build()
}
