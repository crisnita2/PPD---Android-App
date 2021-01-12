package com.example.mobileprojectfinal.movies.movieList

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.mobileprojectfinal.R
import com.example.mobileprojectfinal.auth.data.AuthRepository
import com.example.mobileprojectfinal.core.Properties
import com.example.mobileprojectfinal.core.TAG
import com.example.mobileprojectfinal.movies.data.MovieRepoHelper
import com.example.mobileprojectfinal.movies.data.MovieRepoWorker
import kotlinx.android.synthetic.main.fragment_movie_list.*

class MovieListFragment : Fragment() {
    private lateinit var movieListAdapter: MovieListAdapter
    private lateinit var viewModel: MovieListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v(TAG, "onActivityCreated")
        if (!AuthRepository.isLoggedIn(requireContext())) {
            Log.d(TAG, "is not logged in")
            findNavController().navigate(R.id.fragment_login)
            return
        }
        setupMovieList()
        fab.setOnClickListener {
            Log.v(TAG, "add new movie")
            findNavController().navigate(R.id.fragment_edit_movie)
        }
        logoutBtn.setOnClickListener {
            Log.v(TAG, "log out")
            AuthRepository.logout()
            findNavController().navigate(R.id.fragment_login)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MovieRepoHelper.setViewLifecycleOwner(viewLifecycleOwner)
        Properties.instance.internetActive.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "sending offline actions to server")
            sendOfflineActionsToServer() })
    }

    private fun sendOfflineActionsToServer() {
        val movies = viewModel.movieRepository.movieDao.getAllMovies(AuthRepository.getUsername())
        movies.forEach { movie ->
            if (movie.action == null) {
                movie.action = ""
            }
            if (movie.action != "") {
                Log.d(TAG, "${movie.title} needs ${movie.action}")
                MovieRepoHelper.setMovie(movie)
                var dataParam = Data.Builder().putString("operation", "save")
                when(movie.action) {
                    "update" -> {
                        dataParam = Data.Builder().putString("operation", "update")
                    }
                    "delete" -> {
                        dataParam = Data.Builder().putString("operation", "delete")
                    }
                }
                val request = OneTimeWorkRequestBuilder<MovieRepoWorker>()
                    .setInputData(dataParam.build())
                    .build()
                WorkManager.getInstance(requireContext()).enqueue(request)
            }
        }
    }

    private fun setupMovieList() {
        movieListAdapter = MovieListAdapter(this)
        item_list.adapter = movieListAdapter
        viewModel = ViewModelProvider(this).get(MovieListViewModel::class.java)

        viewModel.movies.observe(viewLifecycleOwner) { movie ->
            Log.v(TAG, "update items")
            Log.d(TAG, "setupItemList items length: ${movie.size}")
            movieListAdapter.movies = movie.filter { it.action != "delete" }
        }

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            Log.i(TAG, "update loading")
            progress.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.loadingError.observe(viewLifecycleOwner) { exception ->
            if (exception != null) {
                Log.i(TAG, "update loading error")
                val message = "Loading exception ${exception.message}"
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.refresh()

        search.doOnTextChanged { _, _, _, _ ->
            viewModel.movies.observe(viewLifecycleOwner, { movie ->
                movieListAdapter.movies = movie
                movieListAdapter.movies =
                    movieListAdapter.searchAndFilter(search.text.toString(), hasSequel.isChecked, noSequel.isChecked)
                movieListAdapter.notifyDataSetChanged()
            })
        }

        hasSequel.setOnClickListener {
            if(hasSequel.isChecked) noSequel.isChecked = false
            viewModel.movies.observe(viewLifecycleOwner, { movie ->
                movieListAdapter.movies = movie
                movieListAdapter.movies =
                    movieListAdapter.searchAndFilter(search.text.toString(), hasSequel.isChecked, noSequel.isChecked)
                movieListAdapter.notifyDataSetChanged()
            })
        }

        noSequel.setOnClickListener {
            if(noSequel.isChecked) hasSequel.isChecked = false
            viewModel.movies.observe(viewLifecycleOwner, { movie ->
                movieListAdapter.movies = movie
                movieListAdapter.movies =
                    movieListAdapter.searchAndFilter(search.text.toString(), hasSequel.isChecked, noSequel.isChecked)
                movieListAdapter.notifyDataSetChanged()
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "onDestroy")
    }
}