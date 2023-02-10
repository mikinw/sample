package com.mnw.deverestinterview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mnw.deverestinterview.model.Movie
import com.mnw.deverestinterview.model.MovieRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repo: MovieRepo
): ViewModel() {

    private val _details = MutableLiveData<Movie?>(null)
    val item : LiveData<Movie?> = _details

    fun setItemId(id: Int) {
        val foundMovie = repo.movies.value?.find { movie -> movie.id == id }
        _details.postValue(foundMovie)
    }

}
