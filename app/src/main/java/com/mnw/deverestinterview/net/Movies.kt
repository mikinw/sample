package com.mnw.deverestinterview.net

import com.google.gson.annotations.SerializedName

data class Movies(

    @SerializedName("page")
    var page: Int,
    @SerializedName("total_results")
    var totalResults: Int,
    @SerializedName("total_pages")
    var totalPages: Int,

    @SerializedName("results")
    var movieList: List<MovieData>? = null,


    )


data class MovieData(
    @SerializedName("id")
    var id: Int,
    @SerializedName("title")
    var title: String,
    @SerializedName("overview")
    var overview: String,
    @SerializedName("poster_path")
    var thumbnail: String,
    @SerializedName("release_date")
    var releaseDate: String,
)
