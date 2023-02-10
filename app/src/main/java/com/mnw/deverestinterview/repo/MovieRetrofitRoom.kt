package com.mnw.deverestinterview.repo

import android.accounts.NetworkErrorException
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
import com.mnw.deverestinterview.net.MoviesDbConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

private fun MovieData.toDatabaseEntity(posterPathBuilder: (String) -> String): MovieRaw {
    return MovieRaw(this.id, this.title, this.overview, this.releaseDate, posterPathBuilder(this.posterPath))
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

                val configuration = moviesApi.getConfiguration()

                val posterPathBuilder =
                    if (configuration.isSuccessful) {
                        val body = configuration.body()
                            ?: throw NetworkErrorException("Can't get configuration data. Body is empty")

                        Log.i("ASD", "${body.imagesConfig.posterSizes}")

                        val selectSize = selectSize(body)
                            ?: throw NetworkErrorException("Configuration does not have a size value")

                        val ret: (String) -> String = { "${body.imagesConfig.secureBaseUrl}$selectSize$it" }
                        ret
                    } else {
                        throw NetworkErrorException("Can't get configuration data")
                    }

                val response = moviesApi.searchMovies("a")

                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        val freshIds = ArrayList<Int>()

                        body.movieList
                            ?.map { movie ->
                                freshIds.add(movie.id)
                                movie.toDatabaseEntity(posterPathBuilder)
                            }
                            ?.toList()
                            ?.let {
                                movieDao.insertAll(it)
                            }

                        movieDao.deleteExcept(freshIds)
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

    private fun selectSize(body: MoviesDbConfiguration) = body.imagesConfig.posterSizes.getOrNull(0)
}