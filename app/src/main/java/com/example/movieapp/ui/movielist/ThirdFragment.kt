package com.example.movieapp.ui.movielist

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentMovieListBinding
import com.example.movieapp.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ThirdFragment : Fragment() {

    private var canLoadMyFeed: Boolean = true
    val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: FragmentMovieListBinding

    @Inject
    lateinit var movieAdapter: MovieListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMovieListBinding.bind(view)
        init()

        //to show the data from network or database
        viewModel.getMovieListData()

        setUpLoadMoreListener()
        setObservers()
    }

    //Setting up pagination for endless feed
    private fun setUpLoadMoreListener() {
        binding.movieRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    //check for scroll down
                    val visibleItemCount =
                        Objects.requireNonNull(recyclerView.layoutManager)!!.childCount
                    val totalItemCount = recyclerView.layoutManager!!.itemCount
                    val pastVisiblesItems =
                        (recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
                    if (canLoadMyFeed) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            canLoadMyFeed = false
                            viewModel.getMovieListData()
                        }
                    }
                }
            }
        })
    }

    //Setting up observer to observe LiveData
    @SuppressLint("LogNotTimber")
    private fun setObservers() {
        viewModel.movieListData.observe(viewLifecycleOwner) { list ->
            list?.let {
                canLoadMyFeed = true
                movieAdapter.submitList(it)
            }

        }
    }

    //Setting up adapter for recyclerView and also setting layout manager for to recyclerView
    private fun init() {
        movieAdapter.onMovieItemClicked { movie ->
            viewModel.selectedMovie = movie
            findNavController().navigate(ThirdFragmentDirections.actionMovieThirdToMovieDetail())
        }
        binding.movieRv.apply {
            adapter = movieAdapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        }
    }
}