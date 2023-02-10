package com.mnw.deverestinterview.repo

import android.accounts.NetworkErrorException
import android.util.Log
import com.mnw.deverestinterview.model.Configuration
import com.mnw.deverestinterview.model.ConfigurationRepo
import com.mnw.deverestinterview.net.MoviesApi
import com.mnw.deverestinterview.net.MoviesDbConfiguration
import javax.inject.Inject


class ConfigRoom @Inject constructor(
    private val moviesApi: MoviesApi,
): ConfigurationRepo {
    override suspend fun getConfig(): Configuration {

        val configuration = moviesApi.getConfiguration()

        if (configuration.isSuccessful) {
            val body = configuration.body()
                ?: throw NetworkErrorException("Can't get configuration data. Body is empty")

            Log.i("ASD", "${body.imagesConfig.posterSizes}")

            val selectSize = selectSize(body)
                ?: throw NetworkErrorException("Configuration does not have a size value")

            return Configuration(body.imagesConfig.secureBaseUrl, selectSize)
        } else {
            throw NetworkErrorException("Can't get configuration data")
        }


    }

    private fun selectSize(body: MoviesDbConfiguration) = body.imagesConfig.posterSizes.getOrNull(0)

}