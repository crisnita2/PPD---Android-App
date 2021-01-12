package com.example.mobileprojectfinal.movies.movieList

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileprojectfinal.R
import com.example.mobileprojectfinal.core.TAG
import com.example.mobileprojectfinal.movies.data.Movie
import kotlinx.android.synthetic.main.view_movie.view.*

class MovieListAdapter(
    private val fragment: Fragment
) : RecyclerView.Adapter<MovieListAdapter.ViewHolder>() {

    var movies = emptyList<Movie>()
        set(value) {
            field = value
            notifyDataSetChanged();
        }

    private var onMovieClick: View.OnClickListener

    init {
        onMovieClick = View.OnClickListener { view ->
            val movie = view.tag as Movie
            fragment.findNavController()
                .navigate(
                    R.id.action_MovieListFragment_to_MovieEditFragment,
                    bundleOf("movie" to movie)
                )
        }
    }

    fun searchAndFilter(substring: String, hasSequel: Boolean, noSequel : Boolean): MutableList<Movie> {
        val filteredList: MutableList<Movie> = ArrayList()
        val substring = substring.toLowerCase().trim()
        for (movie in movies) {
            if (substring.isNotEmpty() && !movie.title.toLowerCase().contains(substring))
                continue
            if(hasSequel && movie.hasSequel!=hasSequel)
                continue
            if(noSequel && movie.hasSequel==noSequel)
                continue
            filteredList.add(movie)
        }
        return filteredList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_movie, parent, false)
        Log.v(TAG, "onCreateViewHolder")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(holder, position)
    }

    override fun getItemCount() = movies.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.title
        private val investment: TextView = view.investment
        private val releaseDate: TextView = view.releaseDate
        private val starIc: ImageView = view.starRating
        private val moviePicture: ImageView = view.moviePicture
        private val latitude: TextView = view.lat
        private val longitude: TextView = view.lon

        fun bind(holder: ViewHolder, position: Int) {
            val movie = movies[position]

            with(holder) {
                itemView.tag = movie
                title.text = movie.title
                investment.text = movie.investment.toString()
                releaseDate.text = movie.releaseDate
                itemView.setOnClickListener(onMovieClick)
                if(movie.hasSequel) starIc.visibility = View.VISIBLE
                else starIc.visibility = View.GONE
                if(movie.picturePath.isNullOrEmpty())
                    moviePicture.visibility = View.GONE
                else {
                    moviePicture.setImageURI(Uri.parse(movie.picturePath))
                    moviePicture.visibility = View.VISIBLE
                }
                if(movie.latitude == null) movie.latitude = 0f
                if(movie.longitude == null) movie.longitude = 0f
                latitude.text = "lat: ${movie.latitude}"
                longitude.text = "long: ${movie.longitude}"
            }
        }
    }
}
