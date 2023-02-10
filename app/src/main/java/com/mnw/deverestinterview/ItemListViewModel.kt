package com.mnw.deverestinterview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnw.deverestinterview.model.MovieRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ItemListViewModel @Inject constructor(
    private val movieRepo: MovieRepo
): ViewModel() {


    init {
        getMovieList()
    }

    val movieList = movieRepo.movies


    fun getMovieList() {
        viewModelScope.launch {
            movieRepo.refreshAll()
        }
    }
}
