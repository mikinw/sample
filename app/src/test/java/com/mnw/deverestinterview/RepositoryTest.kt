package com.mnw.wiseinterview

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import com.mnw.deverestinterview.db.MovieDao
import com.mnw.deverestinterview.db.MovieDatabase
import com.mnw.deverestinterview.db.MovieRaw
import com.mnw.deverestinterview.model.Movie
import com.mnw.deverestinterview.model.NetworkStateModel
import com.mnw.deverestinterview.net.MovieData
import com.mnw.deverestinterview.net.Movies
import com.mnw.deverestinterview.net.MoviesApi
import com.mnw.deverestinterview.repo.MovieRetrofitRoom
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import retrofit2.Response


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class RepositoryTest {

    private lateinit var appDatabase: MovieDatabase
    private lateinit var networkStateModel: NetworkStateModel
    private val mockMoviesApi: MoviesApi = mock()
    private val mockMovieDao: MovieDao = mock()

    @get:Rule
    var instantTaskExecutor = InstantTaskExecutorRule()

    private val scope = TestScope()

    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    lateinit var sut: MovieRetrofitRoom

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)

        val context = RuntimeEnvironment.getApplication()
        appDatabase = Room.inMemoryDatabaseBuilder(context, MovieDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
        appDatabase.close()
    }

    @Test
    fun givesBackWhatIsInsideTheDatabase() {
        // init
        val dummyMovieList = MutableLiveData(
            listOf(
                MovieRaw(1, "titleA", "overviewA", "", ""),
                MovieRaw(2, "titleB", "overviewB", "", ""),
                MovieRaw(3, "titleC", "overviewC", "", ""),
                MovieRaw(4, "titleD", "overviewD", "", ""),
            )
        )
        whenever(mockMovieDao.getAll()).thenReturn(dummyMovieList as LiveData<List<MovieRaw>>)

        networkStateModel = NetworkStateModel(RuntimeEnvironment.getApplication())

        var latestMovieList : List<Movie> = ArrayList()
        val mockObserver = Observer<List<Movie>> {
            latestMovieList = it
        }


        // run
        sut = MovieRetrofitRoom(mockMoviesApi, mockMovieDao, networkStateModel)
        sut.movies.observeForever(mockObserver)

        assertThat(latestMovieList).hasSize(4)
        assertThat(latestMovieList[0].id).isEqualTo(1)
        assertThat(latestMovieList[0].title).isEqualTo("titleA")
        assertThat(latestMovieList[1].id).isEqualTo(2)
        assertThat(latestMovieList[1].title).isEqualTo("titleB")
        assertThat(latestMovieList[2].id).isEqualTo(3)
        assertThat(latestMovieList[2].title).isEqualTo("titleC")
        assertThat(latestMovieList[3].id).isEqualTo(4)
        assertThat(latestMovieList[3].title).isEqualTo("titleD")

    }

    @Test
    fun refreshesListFromApi() {
        val anyNumber = 123456
        runTest {
            launch(Dispatchers.Main) {
                // init
                appDatabase.movieDao().insert(MovieRaw(1, "titleA", "overviewA", "", ""))
                appDatabase.movieDao().insert(MovieRaw(2, "titleB", "overviewB", "", ""))
                appDatabase.movieDao().insert(MovieRaw(3, "titleC", "overviewC", "", ""))
                appDatabase.movieDao().insert(MovieRaw(4, "titleD", "overviewD", "", ""))

                val dummyMovies = Movies(
                    page = anyNumber,
                    totalResults = anyNumber,
                    totalPages = anyNumber,
                    movieList = listOf(
                        MovieData(3, "titleBB", "artistBB", "", ""),
                        MovieData(5, "titleE", "artistE", "", ""),
                        MovieData(6, "titleF", "artistF", "imageId", ""),
                    ))
                whenever(mockMoviesApi.searchMovies(any())).thenReturn(Response.success(dummyMovies))

                networkStateModel = NetworkStateModel(RuntimeEnvironment.getApplication())

                var latestMovieList : List<Movie> = ArrayList()
                val mockObserver = Observer<List<Movie>> {
                    latestMovieList = it
                }


                // run
                sut = MovieRetrofitRoom(mockMoviesApi, appDatabase.movieDao(), networkStateModel)
                sut.movies.observeForever(mockObserver)

                sut.refreshAll(configJob)

                advanceUntilIdle()

                assertThat(latestMovieList).hasSize(3)
                assertThat(latestMovieList[0].id).isEqualTo(3)
                assertThat(latestMovieList[0].title).isEqualTo("titleBB")
                assertThat(latestMovieList[1].id).isEqualTo(5)
                assertThat(latestMovieList[1].title).isEqualTo("titleE")
                assertThat(latestMovieList[2].id).isEqualTo(6)
                assertThat(latestMovieList[2].title).isEqualTo("titleF")
//                assertThat(latestMovieList[2].posterPath).isEqualTo("iiifUrl/imageId/$imageConfig")
            }

        }
    }



}