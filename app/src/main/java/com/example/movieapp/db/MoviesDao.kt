package com.example.movieapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movieapp.entities.MovieApiResponseItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MoviesDao {

    // adding data to database
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(movieEntity: MovieApiResponseItem)


    // calling data from database
    @Query("SELECT* FROM MOVIES")
    fun getMovies(): Flow<List<MovieApiResponseItem>>
}