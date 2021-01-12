package com.example.mobileprojectfinal.movies.movieEdit

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobileprojectfinal.core.Properties
import com.example.mobileprojectfinal.core.Result
import com.example.mobileprojectfinal.core.TAG
import com.example.mobileprojectfinal.movies.data.Movie
import com.example.mobileprojectfinal.movies.data.MovieRepository
import com.example.mobileprojectfinal.movies.data.local.MovieDatabase
import kotlinx.coroutines.launch

class MovieEditViewModel(application: Application) : AndroidViewModel(application) {
    private val mutableFetching = MutableLiveData<Boolean>().apply { value = false }
    private val mutableCompleted = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val fetching: LiveData<Boolean> = mutableFetching
    val fetchingError: LiveData<Exception> = mutableException
    val completed: LiveData<Boolean> = mutableCompleted

    val movieRepository: MovieRepository

    init {
        val movieDao = MovieDatabase.getDatabase(application).movieDao()
        movieRepository = MovieRepository(movieDao)
    }

    fun getMovieById(movieId: String): LiveData<Movie> {
        Log.v(TAG, "getMovieById...")
        return movieRepository.getById(movieId)
    }

    fun saveOrUpdateMovie(movie: Movie) {
        viewModelScope.launch {
            Log.v(TAG, "saveOrUpdateMovie...");
            mutableFetching.value = true
            mutableException.value = null
            val result: Result<Movie>
            if (movie._id.isNotEmpty()) {
                result = movieRepository.update(movie)
            } else {
                result = movieRepository.save(movie)
            }
            when (result) {
                is Result.Success -> {
                    Log.d(TAG, "saveOrUpdateItem succeeded");
                }
                is Result.Error -> {
                    if(result.exception.message?.contains("409")!!){
                        Properties.instance.toastMessage.postValue("There are version conflicts. Please try again")
                    }
                    Log.w(TAG, "saveOrUpdateItem failed", result.exception)
                    mutableException.value = result.exception
                }
            }
            mutableCompleted.value = true
            mutableFetching.value = false
        }
    }

    fun deleteItem(movie: Movie) {
        viewModelScope.launch {
            Log.v(TAG, "deleteItem...");
            val result = movieRepository.delete(movie)
            when (result) {
                is Result.Success -> {
                    Log.d(TAG, "deleteItem succeeded");
                }
                is Result.Error -> {
                    Log.w(TAG, "deleteItem failed", result.exception);
                    mutableException.value = result.exception
                }
            }
        }
    }
}
