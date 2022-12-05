package com.example.movieapp.ui.movielist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.entities.MovieApiResponseItem
import com.example.movieapp.databinding.ItemMovieBinding
import javax.inject.Inject

class MovieListAdapter @Inject constructor() :
    ListAdapter<MovieApiResponseItem, MovieListAdapter.MovieItemViewHolder>(DiffCallback) {

    val differ = AsyncListDiffer(this, DiffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieItemViewHolder {
        return MovieItemViewHolder(
            ItemMovieBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MovieItemViewHolder, position: Int) {
        val movie = getItem(position)
        holder.binding.apply {
            title.text = movie?.title
            desc.text = movie?.plot
            rate.text = "Rating: ${movie?.rating}"
            year.text = movie?.year.toString()
        }

        holder.itemView.setOnClickListener { setMovieItemClickListener?.let { it(movie) } }
    }

    class MovieItemViewHolder(val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root)

    private var setMovieItemClickListener: ((movie: MovieApiResponseItem) -> Unit)? = null

    fun onMovieItemClicked(listener: (MovieApiResponseItem) -> Unit) {
        setMovieItemClickListener = listener
    }


    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<MovieApiResponseItem>() {
            override fun areItemsTheSame(
                oldItem: MovieApiResponseItem,
                newItem: MovieApiResponseItem
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: MovieApiResponseItem,
                newItem: MovieApiResponseItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}