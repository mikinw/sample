package com.mnw.deverestinterview.model

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Deferred


interface MovieRepo {

    val movies: LiveData<List<Movie>>

    suspend fun refreshAll(configJob: Deferred<((String) -> String)?>)
    suspend fun getDetails(id: Int, configJob: Deferred<((String) -> String)?>)
}