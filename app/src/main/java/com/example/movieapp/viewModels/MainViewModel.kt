package com.example.movieapp.viewModels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
class MainViewModel @Inject constructor(
    application: Application,
    private val networkRepository: NetworkRepository,
    private val dataBaseRepository: DataBaseRepository
) :
    AndroidViewModel(application) {

    private var _movieListData: MutableLiveData<List<MovieApiResponseItem>> = MutableLiveData()
    val movieListData: LiveData<List<MovieApiResponseItem>> = _movieListData
    var selectedMovie: MovieApiResponseItem? = null
    private var pageNumber = 1

    init {
        getRatingData()
    }


    //fetching the movieList data from server and saving it in model and also saving the data in database
    @SuppressLint("LogNotTimber")
    fun getMovieListData() {
        viewModelScope.launch {
            //Using if else with check is device is connected with internet or not
            //to show the data from network or database
            if (isInternetAvailable()) {
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
            } else {
                //fetching data from database and saving it in model
                dataBaseRepository.getMovies().collectLatest { movies ->
                    _movieListData.postValue(movies)
                }
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


    //method to check weather device is connected to internet or not
    @SuppressLint("NewApi")
    fun isInternetAvailable(): Boolean {
        var result = false
        val connectivity =
            getApplication<Application>().applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

        connectivity?.let {
            it.getNetworkCapabilities(connectivity.activeNetwork)?.apply {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            }
        }
        return result
    }



}