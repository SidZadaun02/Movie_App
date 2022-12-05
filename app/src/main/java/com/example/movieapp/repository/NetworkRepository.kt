package com.example.movieapp.repository

import com.example.movieapp.apiservice.MovieApiService
import com.example.movieapp.entities.MovieApiResponseItem
import com.example.movieapp.data.response.RatingListResponseItem
import retrofit2.Response
import javax.inject.Inject

class NetworkRepository @Inject constructor(private val movieApiService: MovieApiService) {

    //calling data from server for movie list
    suspend fun getMovieList(page: Int): Response<List<MovieApiResponseItem>> {
        return movieApiService.getMovieData(page)
    }

    //calling data from server for rating list for polling
    suspend fun getMovieRating(): Response<List<RatingListResponseItem>> {
        return movieApiService.getMovieRating()
    }
}