package com.mnw.deverestinterview.model

import android.util.Log
import kotlinx.coroutines.*
import javax.inject.Inject


class RefreshMoviesUseCase constructor(
    private val configRepository: ConfigurationRepo,
    private val movieRepo: MovieRepo,
    private val networkState: NetworkStateModel,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    @Inject constructor(
        configRepository: ConfigurationRepo,
        movieRepo: MovieRepo,
        networkState: NetworkStateModel,
    ) : this(configRepository, movieRepo, networkState, Dispatchers.Default)


    suspend operator fun invoke(query: String) {
        withContext(dispatcher) {

            try {

                networkState.requestState(NetworkState.REFRESHING)

                val configJob = async {
                    var ret: ((String) -> String)? = null
                    try {
                        val config = configRepository.getConfig()
                        val posterPathBuilder: (String) -> String = { "${config.basePath}${config.size}$it" }
                        ret = posterPathBuilder
                    } catch (ex: Exception) {
                        Log.e("ASD", "configJob ex: ${ex.localizedMessage}")
                        networkState.requestState(NetworkState.ERROR, ex.localizedMessage)

                    }
                    ret
                }

                launch {
                    try {

                        movieRepo.refreshAll(query, configJob)
                    } catch (ex: Exception) {
                        Log.e("ASD", "refreshall ex: ${ex.localizedMessage}")
                        ex.printStackTrace()
                        networkState.requestState(NetworkState.ERROR, ex.localizedMessage)
                    }
                }.join()


                launch {
                    movieRepo.movies.value.orEmpty().map { movie -> movie.id }.forEach {
                        launch {
                            try {
                                movieRepo.getDetails(it, configJob)
                            } catch (ex: Exception) {
                                Log.e("ASD", "refreshdetails ex: ${ex.localizedMessage}")
                                networkState.requestState(NetworkState.ERROR, ex.localizedMessage)
                            }
                        }
                    }
                }.join()

                networkState.requestState(NetworkState.NO_ACTIVITY)

            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.e("ASD", ex.localizedMessage as String)
                networkState.requestState(NetworkState.ERROR, ex.localizedMessage)


            }

        }
    }
}