package com.mnw.deverestinterview.net

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface MoviesApi {

    @GET("/3/search/movie")
    suspend fun searchMovies(
        @Query("query") search: String
    ): Response<Movies>

    @GET("/3/configuration")
    suspend fun getConfiguration(): Response<MoviesDbConfiguration>
}