package com.mnw.deverestinterview.repo

import android.accounts.NetworkErrorException
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.mnw.deverestinterview.db.MovieDao
import com.mnw.deverestinterview.db.MovieRaw
import com.mnw.deverestinterview.model.Movie
import com.mnw.deverestinterview.model.MovieRepo
import com.mnw.deverestinterview.net.MovieData
import com.mnw.deverestinterview.net.MoviesApi
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

private fun MovieData.toDatabaseEntity(posterPathBuilder: (String) -> String): MovieRaw {
    return MovieRaw(this.id, this.title, this.overview, this.releaseDate, posterPathBuilder(this.posterPath), this.budget)
}

private fun MovieRaw.asDomainModel(): Movie {
    return Movie(this.id, this.title, this.overview, this.releaseDate, this.posterPath, this.budget)
}

@Singleton
class MovieRetrofitRoom constructor(
    private val moviesApi: MoviesApi,
    private val movieDao: MovieDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,

): MovieRepo {

    @Inject constructor(
        moviesApi: MoviesApi,
        movieDao: MovieDao,
    ) : this(moviesApi, movieDao, Dispatchers.IO)

    override val movies: LiveData<List<Movie>> = Transformations.map(movieDao.getAll()) {
        Log.i("ASD", "Transformations")

        it.map { raw -> raw.asDomainModel() }.toList()
    }

    override suspend fun refreshAll(configJob: Deferred<(String) -> String>) {
        withContext(dispatcher) {

            val response = moviesApi.searchMovies("a")

            if (response.isSuccessful) {
                response.body()?.let { body ->
                    val freshIds = ArrayList<Int>()

                    val posterPathBuilder = configJob.await()


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
                    Log.i("ASD", "refreshAll done")

                }


            } else {

                Log.e("ASD", "could not fetch api: ${response.errorBody().toString()}")
                throw NetworkErrorException(response.errorBody().toString())

            }


        }

    }

    override suspend fun getDetails(id: Int, configJob: Deferred<(String) -> String>) {

        withContext(dispatcher) {

            val response = moviesApi.getDetails(id)

            if (response.isSuccessful) {
                val movieData = response.body()
                    ?: throw NetworkErrorException("Can't get detail for $id")

                val posterPathBuilder = configJob.await()

                val movieRaw = movieData.toDatabaseEntity(posterPathBuilder)

                Log.i("ASD", "new budget for $id: ${movieRaw.budget}")
                movieDao.insert(movieRaw)


            } else {

                Log.e("ASD", "could not fetch api: ${response.errorBody().toString()}")
                throw NetworkErrorException(response.errorBody().toString())

            }


        }
    }

}