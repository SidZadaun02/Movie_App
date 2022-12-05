package com.example.movieapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.movieapp.entities.MovieApiResponseItem


@Database(
    entities = [MovieApiResponseItem::class],
    version = 1,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun getMoviesDao() : MoviesDao
}


