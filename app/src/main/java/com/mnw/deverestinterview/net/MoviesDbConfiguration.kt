package com.mnw.deverestinterview.net

import com.google.gson.annotations.SerializedName

data class MoviesDbConfiguration(
        @SerializedName("images")
        val imagesConfig: ImagesConfig,
)

data class ImagesConfig(
        @SerializedName("base_url")
        val baseUrl: String,
        @SerializedName("secure_base_url")
        val secureBaseUrl: String,
        @SerializedName("poster_sizes")
        val posterSizes: List<String>
)
