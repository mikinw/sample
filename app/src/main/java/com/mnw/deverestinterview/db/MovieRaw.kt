package com.mnw.deverestinterview.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "movie")
data class MovieRaw(
    @PrimaryKey
    val id: Int,
    val title: String,
    val overview: String,
    val releaseDate: String,
    val thumbnail: String?,
)