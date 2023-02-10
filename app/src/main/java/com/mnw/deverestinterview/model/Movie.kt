package com.mnw.deverestinterview.model

import android.graphics.Bitmap


data class Movie(
    val id: String,
    val title: String,
    val overview: String,
    val releaseDate: String = "",
    val thumbnail: Bitmap? = null
)