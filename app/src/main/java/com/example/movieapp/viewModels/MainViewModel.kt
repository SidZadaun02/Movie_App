package com.example.movieapp.viewModels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.*
import com.example.movieapp.data.response.RatingListResponseItem
import com.example.movieapp.entities.MovieApiResponseItem
import com.example.movieapp.repository.DataBaseRepository
import com.example.movieapp.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val networkRepository: NetworkRepository,private val dataBaseRepository: DataBaseRepository) :
    ViewModel() {

    private var _movieListData: MutableLiveData<List<MovieApiResponseItem>> = MutableLiveData()
    val movieListData: LiveData<List<MovieApiResponseItem>> = _movieListData

    var selectedMovie: MovieApiResponseItem? = null
    var pageNumber = 1

    init {
        getRatingData()
    }

    //fetching the movieList data from server and saving it in model and also saving the data in database
    @SuppressLint("LogNotTimber")
    fun getMovieListData() {
        viewModelScope.launch {
            val response = networkRepository.getMovieList(pageNumber)
            if (response.isSuccessful) {
                if (response.body()?.size != 0) {
                    pageNumber++
                }

                val list = mutableListOf<MovieApiResponseItem>()
                _movieListData.value?.toMutableList()?.let { list.addAll(it) }
                response.body()?.toMutableList()?.let { list.addAll(it) }
                _movieListData.postValue(list)
                insertMovie(list)

            } else {
                Log.d("errorResponse***", "${response.errorBody()}")
            }
        }
    }

    //fetching data from database and saving it in model
    fun getDataBaseData(){
        viewModelScope.launch {
            dataBaseRepository.getMovies().collectLatest { movies ->
                _movieListData.postValue(movies)
            }
        }
    }

    //method for saving the data into database
    private fun insertMovie(movie: List<MovieApiResponseItem>) {
        viewModelScope.launch {
            for (i in movie.indices) {
                dataBaseRepository.insertMovie(movie[i])
            }
        }
    }


    //fetching the rating data from server and also setting it in LiveData with changes in every minute
    //comparing with ids of movie to change the rating of movies
    @SuppressLint("LogNotTimber")
    fun getRatingData() {
        viewModelScope.launch {
            while (true) {
                delay(60000)
                val response = networkRepository.getMovieRating()
                withContext(Dispatchers.Default) {
                    if (response.isSuccessful) {
                        val ratingList = mutableListOf<RatingListResponseItem>()
                        response.body()?.toMutableList()?.let { ratingList.addAll(it) }
                        val list = _movieListData.value
                        ratingList.forEach { ratingItem ->
                            val movieApiResponseItem = list?.find { it.id == ratingItem.id }
                            movieApiResponseItem?.let {
                                it.rating = ratingItem.rating
                            }
                        }
                        withContext(Dispatchers.Main) {
                            list?.let {
                                _movieListData.postValue(it)
                            }
                        }
                    } else {
                        Log.d("ratingError****", "${response.errorBody()}")
                    }
                }
            }
        }
    }

}