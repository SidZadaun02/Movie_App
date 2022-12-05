package com.example.movieapp.repository

import com.example.movieapp.db.AppDatabase
import com.example.movieapp.entities.MovieApiResponseItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataBaseRepository @Inject constructor(private val appDatabase: AppDatabase) {

    // calling data from database
    fun getMovies(): Flow<List<MovieApiResponseItem>> {
        return appDatabase.getMoviesDao().getMovies()
    }

    // saving data to database
    suspend fun insertMovie(movie: MovieApiResponseItem) {
        return appDatabase.getMoviesDao().insert(movie)
    }
}