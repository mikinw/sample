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


    suspend operator fun invoke() {
        withContext(dispatcher) {

            try {

                networkState.requestState(NetworkState.REFRESHING)

                val configJob = async {
                    val config = configRepository.getConfig()
                    val posterPathBuilder: (String) -> String = { "${config.basePath}${config.size}$it" }
                    posterPathBuilder
                }

                val refreshListJob = launch {
                    movieRepo.refreshAll(configJob)
                }

                refreshListJob.join()


                networkState.requestState(NetworkState.NO_ACTIVITY)

            } catch (ex: Exception) {
                ex.printStackTrace()
                networkState.requestState(NetworkState.ERROR, ex.localizedMessage)


            }

        }
    }
}