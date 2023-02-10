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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

private fun MovieData.toDatabaseEntity(posterPathBuilder: (String) -> String): MovieRaw {
    val poster = if (this.posterPath != null) posterPathBuilder(this.posterPath) else ""
    return MovieRaw(this.id, this.title, this.overview, this.releaseDate, poster, this.budget)
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
        it.map { raw -> raw.asDomainModel() }.toList()
    }

    override suspend fun refreshAll(query: String, configJob: Deferred<((String) -> String)?>): List<Int> {
        return withContext(dispatcher) {
            Log.i("ASD", "refreshAll: $query")

            val freshIds = ArrayList<Int>()
            val response = moviesApi.searchMovies(query)

            if (response.isSuccessful) {
                response.body()?.let { body ->

                    val posterPathBuilder = configJob.await()
                        ?: throw NetworkErrorException("Fetching configuration was unsuccessful")


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
                    Log.i("ASD", "refreshAll done $freshIds")


                }
                freshIds


            } else {

                Log.e("ASD", "could not fetch api: ${response.errorBody().toString()}")
                throw NetworkErrorException(response.errorBody().toString())

            }




        }

    }

    override suspend fun getDetails(id: Int, configJob: Deferred<((String) -> String)?>) {

        withContext(dispatcher) {

            val response = moviesApi.getDetails(id)

            if (response.isSuccessful) {
                val movieData = response.body()
                    ?: throw NetworkErrorException("Can't get detail for $id")

                val posterPathBuilder = configJob.await()
                    ?: throw NetworkErrorException("Fetching configuration was unsuccessful")

                val movieRaw = movieData.toDatabaseEntity(posterPathBuilder)

                movieDao.insert(movieRaw)


            } else {

                Log.e("ASD", "could not fetch api: ${response.errorBody().toString()}")
                throw NetworkErrorException(response.errorBody().toString())

            }


        }
    }

}