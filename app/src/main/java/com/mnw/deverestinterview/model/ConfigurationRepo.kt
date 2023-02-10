package com.mnw.deverestinterview.model

interface ConfigurationRepo {
    suspend fun getConfig(): Configuration

}

data class Configuration(
    val basePath: String,
    val size: String,
)
