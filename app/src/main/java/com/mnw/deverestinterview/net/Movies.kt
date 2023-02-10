package com.mnw.deverestinterview.net

import com.google.gson.annotations.SerializedName

data class Movies(

    @SerializedName("page")
    val page: Int,
    @SerializedName("total_results")
    val totalResults: Int,
    @SerializedName("total_pages")
    val totalPages: Int,

    @SerializedName("results")
    val movieList: List<MovieData>? = null,


    )


data class MovieData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("budget")
    val budget: Int?,
)
