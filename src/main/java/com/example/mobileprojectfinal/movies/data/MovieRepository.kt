package com.example.mobileprojectfinal.movies.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.mobileprojectfinal.auth.data.AuthRepository
import com.example.mobileprojectfinal.core.Properties
import com.example.mobileprojectfinal.core.Result
import com.example.mobileprojectfinal.movies.data.local.MovieDao
import com.example.mobileprojectfinal.movies.data.remote.MovieApi

class MovieRepository(val movieDao: MovieDao) {

    var movies = MediatorLiveData<List<Movie>>().apply { postValue(emptyList()) }

    suspend fun refresh(): Result<Boolean> {
        try {
            if (Properties.instance.internetActive.value!!) {
                val moviesApi = MovieApi.service.find()
                movies.value = moviesApi
                for (movie in moviesApi) {
                    movie.owner = AuthRepository.getUsername()
                    movieDao.insert(movie)
                }
            } else
                movies.addSource(movieDao.getAll(AuthRepository.getUsername())) {
                    movies.value = it
                }
            return Result.Success(true)
        } catch (e: Exception) {
            movies.addSource(movieDao.getAll(AuthRepository.getUsername())) {
                movies.value = it
            }
            return Result.Error(e)
        }
    }

    fun getById(movieId: String): LiveData<Movie> {
        return movieDao.getById(movieId)
    }

    suspend fun save(movie: Movie): Result<Movie> {
        try {
            if (Properties.instance.internetActive.value!!) {
                val createdMovie = MovieApi.service.create(
                    MovieDTO(
                        movie.title,
                        movie.investment,
                        movie.releaseDate,
                        movie.hasSequel,
                        movie.owner,
                    )
                )
                createdMovie.action = ""
                movieDao.insert(createdMovie)
                return Result.Success(createdMovie)
            } else {
                movie.action = "save"
                movieDao.insert(movie)
                Properties.instance.toastMessage.postValue("Movie was saved locally. It will be saved on the server once you connect to the internet")
                return Result.Success(movie)
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    suspend fun update(movie: Movie): Result<Movie> {
        try {
            if (Properties.instance.internetActive.value!!) {
                val updatedMovie = MovieApi.service.update(movie._id, movie)
                updatedMovie.action = ""
                movieDao.update(updatedMovie)
                return Result.Success(updatedMovie)
            }
            else {
                movie.action = "update"
                movieDao.update(movie)
                Properties.instance.toastMessage.postValue("Movie was updated locally. It will be updated to the server once you connect to the internet")
                return Result.Success(movie)
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    suspend fun delete(movie: Movie): Result<Boolean> {
        try {
            if (Properties.instance.internetActive.value!!) {

                MovieApi.service.delete(movie._id)
                movieDao.delete(movie._id)
                return Result.Success(true)
            }
            else{
                movie.action = "delete"
                movieDao.update(movie)
                Properties.instance.toastMessage.postValue("Movie was deleted locally. It will be deleted to the server once you connect to the internet")
                return Result.Success(true)
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }
}