package com.mnw.deverestinterview

import android.util.Log
import androidx.lifecycle.*
import com.mnw.deverestinterview.model.Movie
import com.mnw.deverestinterview.net.MoviesApi
import com.mnw.deverestinterview.net.MoviesClient
import com.mnw.deverestinterview.placeholder.PlaceholderContent
import kotlinx.coroutines.launch


class ItemListViewModel: ViewModel() {

    private val _movieList = MutableLiveData<List<Movie>>()
    val movieList: LiveData<List<Movie>>
        get() = _movieList


    fun getMovieList() {
        val retrofit = MoviesClient.getInstance()
        val apiInterface = retrofit.create(MoviesApi::class.java)
        viewModelScope.launch {
            try {
                val response = apiInterface.searchMovies("a")
                if (response.isSuccessful) {

                    PlaceholderContent.clear()
                    _movieList.value = response.body()
                        ?.movieList
                        ?.map { movie ->
                            Movie(movie.thumbnail.substring(0,4), movie.title, movie.overview, movie.releaseDate)
                        }
                        ?.toList()

                } else {
                    Log.e("Error", response.errorBody().toString())
                }
            } catch (Ex:Exception){
                Log.e("Error", Ex.localizedMessage)
            }
        }
    }
}
