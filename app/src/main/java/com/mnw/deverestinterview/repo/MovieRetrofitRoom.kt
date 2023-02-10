package com.mnw.deverestinterview.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.mnw.deverestinterview.db.MovieDao
import com.mnw.deverestinterview.db.MovieRaw
import com.mnw.deverestinterview.model.Movie
import com.mnw.deverestinterview.model.MovieRepo
import com.mnw.deverestinterview.model.NetworkState
import com.mnw.deverestinterview.model.NetworkStateModel
import com.mnw.deverestinterview.net.MovieData
import com.mnw.deverestinterview.net.MoviesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

private fun MovieData.toDatabaseEntity(): MovieRaw {
    val imageUrl = "https://image.tmdb.org/t/p/w342${this.posterPath}"
    return MovieRaw(this.id, this.title, this.overview, this.releaseDate, imageUrl)
}

private fun MovieRaw.asDomainModel(): Movie {
    return Movie(this.id, this.title, this.overview, this.releaseDate, this.posterPath)
}

class MovieRetrofitRoom @Inject constructor(
    private val moviesApi: MoviesApi,
    private val movieDao: MovieDao,
    private val networkState: NetworkStateModel,

    ): MovieRepo {

    override val movies: LiveData<List<Movie>> = Transformations.map(movieDao.getAll()) {
        it.map { raw -> raw.asDomainModel() }.toList()
    }

    override suspend fun refreshAll() {
        withContext(Dispatchers.IO) {
            try {
                networkState.requestState(NetworkState.REFRESHING)

                val response = moviesApi.searchMovies("a")

                if (response.isSuccessful) {
                    response.body()?.movieList
                        ?.map { movie ->
                            movie.toDatabaseEntity()
                        }
                        ?.toList()
                        ?.let {
                            movieDao.insertAll(it)
                        }

                    networkState.requestState(NetworkState.NO_ACTIVITY)


                } else {

                    Log.e("ASD", "could not fetch api: ${response.errorBody().toString()}")
                    networkState.requestState(NetworkState.ERROR, response.errorBody().toString())

                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                networkState.requestState(NetworkState.ERROR, ex.localizedMessage)


            }
        }

    }
}