package com.example.movieapp.repository

import com.example.movieapp.db.AppDatabase
import com.example.movieapp.entities.MovieApiResponseItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataBaseRepository @Inject constructor(private val appDatabase: AppDatabase) {



    fun getMovies(): Flow<List<MovieApiResponseItem>> {
        return appDatabase.getMoviesDao().getMovies()
    }

    suspend fun insertMovie(movie: MovieApiResponseItem) {
        return appDatabase.getMoviesDao().insert(movie)
    }
}