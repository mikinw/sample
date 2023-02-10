package com.mnw.deverestinterview.model


data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val releaseDate: String = "",
    val posterPath: String = "",
    val budget: Int? = null,
)