package com.mnw.deverestinterview.repo

import android.accounts.NetworkErrorException
import android.util.Log
import com.mnw.deverestinterview.model.Configuration
import com.mnw.deverestinterview.model.ConfigurationRepo
import com.mnw.deverestinterview.net.MoviesApi
import com.mnw.deverestinterview.net.MoviesDbConfiguration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ConfigRoom constructor(
    private val moviesApi: MoviesApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
): ConfigurationRepo {

    @Inject constructor(
        moviesApi: MoviesApi,
    ): this(moviesApi, Dispatchers.IO)

    override suspend fun getConfig(): Configuration {
        return withContext(dispatcher) {

            val configuration = moviesApi.getConfiguration()

            if (configuration.isSuccessful) {
                val body = configuration.body()
                    ?: throw NetworkErrorException("Can't get configuration data. Body is empty")

                Log.i("ASD", "${body.imagesConfig.posterSizes}")

                val selectSize = selectSize(body)
                    ?: throw NetworkErrorException("Configuration does not have a size value")

                Configuration(body.imagesConfig.secureBaseUrl, selectSize)
            } else {
                throw NetworkErrorException("Can't get configuration data")
            }
        }


    }

    private fun selectSize(body: MoviesDbConfiguration) = body.imagesConfig.posterSizes.getOrNull(2)

}