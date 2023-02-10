package com.mnw.deverestinterview.di

import android.app.Application
import androidx.room.Room
import com.mnw.deverestinterview.db.MovieDao
import com.mnw.deverestinterview.db.MovieDatabase
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
    fun provideDatabase(application: Application): MovieDatabase {
        return Room.databaseBuilder(application, MovieDatabase::class.java, "movies.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideArtworkDao(db: MovieDatabase): MovieDao {
        return db.movieDao()
    }
}

