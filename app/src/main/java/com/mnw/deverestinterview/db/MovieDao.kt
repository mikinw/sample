package com.mnw.deverestinterview.db

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(movie: MovieRaw)

    @Query("DELETE FROM movie WHERE id = :movieId")
    suspend fun deleteById(movieId: String)

    @Delete
    suspend fun delete(movie: MovieRaw)

    @Query("SELECT * FROM movie")
    fun getAll(): LiveData<List<MovieRaw>>
    @Query("SELECT * FROM movie WHERE id = :movie")
    fun getById(movie: Int): MovieRaw

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<MovieRaw>)

    @Query("DELETE FROM movie WHERE id NOT IN (:freshIds)")
    fun deleteExcept(freshIds: List<Int>): Int

    @Query("DELETE FROM movie")
    fun deleteAll()

}