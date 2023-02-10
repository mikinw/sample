package com.mnw.deverestinterview.di

import com.mnw.deverestinterview.model.ConfigurationRepo
import com.mnw.deverestinterview.model.MovieRepo
import com.mnw.deverestinterview.repo.ConfigRoom
import com.mnw.deverestinterview.repo.MovieRetrofitRoom
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMovieRepo(movieRetrofitRoom: MovieRetrofitRoom): MovieRepo

    @Binds
    @Singleton
    abstract fun bindConfigRepo(configRetrofit: ConfigRoom): ConfigurationRepo

}