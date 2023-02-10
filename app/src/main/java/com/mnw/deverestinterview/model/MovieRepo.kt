package com.mnw.deverestinterview.model

import androidx.lifecycle.LiveData


interface MovieRepo {

    val movies: LiveData<List<Movie>>

    suspend fun refreshAll()
}