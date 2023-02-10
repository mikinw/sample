package com.mnw.deverestinterview.di

import com.mnw.deverestinterview.net.MoviesApi
import com.mnw.deverestinterview.net.MoviesClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideNetworkApi(): MoviesApi {
        return MoviesClient.getInstance().create(MoviesApi::class.java)
    }
}