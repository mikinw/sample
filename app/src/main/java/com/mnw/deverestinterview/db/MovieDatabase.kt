package com.mnw.deverestinterview.db

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [MovieRaw::class], version = 3)
abstract class MovieDatabase: RoomDatabase() {
    abstract fun movieDao(): MovieDao
}