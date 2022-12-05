package com.example.movieapp.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "MOVIES",indices = [Index(value = ["id"], unique = true)])
data class MovieApiResponseItem(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var actors: String,
    var director: String,
    var plot: String,
    var posterUrl: String,
    var  rating: Double,
    val runtime: Int,
    var title: String,
    var year: Int
) {
    override fun toString(): String {
        return "MovieApiResponseItem(actors='$actors', director='$director', id=$id, plot='$plot', posterUrl='$posterUrl', rating=$rating, runtime=$runtime, title='$title', year=$year)"
    }
}