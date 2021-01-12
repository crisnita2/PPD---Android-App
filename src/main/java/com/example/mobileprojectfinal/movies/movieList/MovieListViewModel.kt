package com.example.mobileprojectfinal.movies.movieList

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.mobileprojectfinal.core.TAG
import com.example.mobileprojectfinal.core.Result
import com.example.mobileprojectfinal.core.Properties
import com.example.mobileprojectfinal.movies.data.Movie
import com.example.mobileprojectfinal.movies.data.MovieRepoHelper
import com.example.mobileprojectfinal.movies.data.MovieRepository
import com.example.mobileprojectfinal.movies.data.local.MovieDatabase
import com.example.mobileprojectfinal.movies.data.remote.RemoteDataSource
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class MovieListViewModel(application: Application) : AndroidViewModel(application) {
    private val mutableLoading = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val movieRepository: MovieRepository
    var movies: LiveData<List<Movie>>
    val loading: LiveData<Boolean> = mutableLoading
    val loadingError: LiveData<Exception> = mutableException

    init {
        val movieDao = MovieDatabase.getDatabase(application).movieDao()
        movieRepository = MovieRepository(movieDao)
        movies = movieRepository.movies

        MovieRepoHelper.setMovieRepo(movieRepository)

        val request = Request.Builder().url("ws://192.168.0.103:3000").build()
        OkHttpClient().newWebSocket(
            request,
            RemoteDataSource.MyWebSocketListener(application.applicationContext)
        )
        CoroutineScope(Dispatchers.Main).launch { collectEvents() }
    }

    fun refresh() {
        viewModelScope.launch {
            Log.v(TAG, "refresh...");
            mutableLoading.value = true
            mutableException.value = null
            when (val result = movieRepository.refresh()) {
                is Result.Success -> {
                    Log.d(TAG, "refresh succeeded");
                }
                is Result.Error -> {
                    Log.w(TAG, "refresh failed", result.exception);
                    mutableException.value = result.exception
                }
            }
            mutableLoading.value = false
        }
    }

    private suspend fun collectEvents() {
        while (true) {
            val res = JSONObject(RemoteDataSource.eventChannel.receive())
            val movie = Gson().fromJson(res.getJSONObject("payload").toString(), Movie::class.java)
            Log.d("ws", "received $movie")
            refresh()
        }
    }
}
