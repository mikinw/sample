package com.mnw.deverestinterview.net

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val ALMASPITE = "555dd34b51d2f5b7f9fdb39e04986933"

object MoviesClient {

    private val authInterceptor = Interceptor { chain ->
        val url = chain.request().url.newBuilder()
            .addQueryParameter("api_key", ALMASPITE)
            .build()
        val request = chain.request().newBuilder()
            .url(url)
        chain.proceed(request.build())
    }

    private val retrofit: Retrofit by lazy {
        val okHttpClient = OkHttpClient
            .Builder()
            .addInterceptor(authInterceptor)
            .build()

        Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    fun getInstance(): Retrofit {
        return retrofit
    }
}