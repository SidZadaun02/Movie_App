package com.example.movieapp.apiservice

import com.example.movieapp.entities.MovieApiResponseItem
import com.example.movieapp.data.response.RatingListResponseItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApiService {

    //to get movie data
    @GET("movieList.php")
    suspend fun getMovieData(
        @Query("page") page: Int
    ): Response<List<MovieApiResponseItem>>

    //to get rating data for polling
    @GET("ratingUpdate.php")
    suspend fun getMovieRating(): Response<List<RatingListResponseItem>>

}