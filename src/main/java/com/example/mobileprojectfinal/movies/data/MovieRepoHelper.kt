package com.example.mobileprojectfinal.movies.data

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.mobileprojectfinal.core.Properties
import com.example.mobileprojectfinal.core.Result
import com.example.mobileprojectfinal.core.TAG
import com.example.mobileprojectfinal.movies.data.remote.MovieApi
import kotlinx.coroutines.launch

object MovieRepoHelper {
    var movieRepository: MovieRepository? = null
    private var movie: Movie? = null
    private var viewLifecycleOwner: LifecycleOwner? = null

    fun setMovieRepo(movieParam: MovieRepository) {
        this.movieRepository = movieParam
    }

    fun setMovie(movieParam: Movie) {
        this.movie = movieParam
    }

    fun setViewLifecycleOwner(viewLifecycleOwnerParam: LifecycleOwner) {
        viewLifecycleOwner = viewLifecycleOwnerParam
    }

    fun save() {
        viewLifecycleOwner!!.lifecycleScope.launch {
            saveHelper()
        }
    }

    private suspend fun saveHelper(): Result<Movie> {
        try {
            if (Properties.instance.internetActive.value!!) {

                val createdMovie = MovieApi.service.create(MovieDTO(
                    movie?.title!!,
                    movie?.investment!!,
                    movie?.releaseDate!!,
                    movie?.hasSequel!!,
                    movie?.owner,
                ))

                createdMovie.action = ""
                movieRepository!!.movieDao.deleteMovie(createdMovie.title, createdMovie.releaseDate)
                movieRepository!!.movieDao.insert(createdMovie)
                Properties.instance.toastMessage.postValue("Movie was saved on the server")
                return Result.Success(createdMovie)
            } else {
                Log.d(TAG, "internet still not working...")
                return Result.Error(Exception("internet still not working..."))
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    fun update() {
        viewLifecycleOwner!!.lifecycleScope.launch {
            updateHelper()
        }
    }

    private suspend fun updateHelper(): Result<Movie> {
        try {
            if (Properties.instance.internetActive.value!!) {
                Log.d(TAG, "updateNewVersionHelper")
                movie!!.action = ""
                val updatedMovie = MovieApi.service.update(movie!!._id, movie!!)
                movieRepository!!.movieDao.update(updatedMovie)
                Properties.instance.toastMessage.postValue("Movie was updated on the server")
                return Result.Success(updatedMovie)
            } else {
                Log.d(TAG, "internet still not working...")
                return Result.Error(Exception("internet still not working..."))
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    fun delete(){
        viewLifecycleOwner!!.lifecycleScope.launch {
            deleteHelper()
        }
    }

    private suspend fun deleteHelper(): Result<Boolean> {
        try {
            if (Properties.instance.internetActive.value!!) {
                Log.d(TAG, "updateNewVersionHelper")
                MovieApi.service.delete(movie!!._id)
                movieRepository!!.movieDao.delete(movie!!._id)
                Properties.instance.toastMessage.postValue("Movie was deleted on the server")
                return Result.Success(true)
            } else {
                Log.d(TAG, "internet still not working...")
                return Result.Error(Exception("internet still not working..."))
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }
}