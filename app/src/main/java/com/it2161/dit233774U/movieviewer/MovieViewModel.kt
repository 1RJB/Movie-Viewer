package com.it2161.dit233774U.movieviewer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository(application)

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _movieDetails = MutableStateFlow<Movie?>(null)
    val movieDetails: StateFlow<Movie?> = _movieDetails

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    private val _favoriteMovies = MutableStateFlow<List<FavoriteMovie>>(emptyList())
    val favoriteMovies: StateFlow<List<FavoriteMovie>> = _favoriteMovies

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    fun getPopularMovies() {
        viewModelScope.launch {
            val response = repository.getPopularMovies()
            _movies.value = response.results
        }
    }

    fun getTopRatedMovies() {
        viewModelScope.launch {
            val response = repository.getTopRatedMovies()
            _movies.value = response.results
        }
    }

    fun getNowPlayingMovies() {
        viewModelScope.launch {
            val response = repository.getNowPlayingMovies()
            _movies.value = response.results
        }
    }

    fun getUpcomingMovies() {
        viewModelScope.launch {
            val response = repository.getUpcomingMovies()
            _movies.value = response.results
        }
    }

    fun getMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _movieDetails.value = repository.getMovieDetails(movieId)
        }
    }

    fun getMovieReviews(movieId: Int) {
        viewModelScope.launch {
            val response = repository.getMovieReviews(movieId)
            _reviews.value = response.results
        }
    }

    fun searchMovies(query: String) {
        viewModelScope.launch {
            val response = repository.searchMovies(query)
            _movies.value = response.results
        }
    }

    fun getSimilarMovies(movieId: Int) {
        viewModelScope.launch {
            val response = repository.getSimilarMovies(movieId)
            _movies.value = response.results
        }
    }

    fun registerUser(userId: String, password: String, preferredName: String) {
        viewModelScope.launch {
            val user = User(userId, password, preferredName)
            repository.registerUser(user)
        }
    }

    fun loginUser(userId: String, password: String) {
        viewModelScope.launch {
            val user = repository.loginUser(userId, password)
            _currentUser.value = user
            if (user != null) {
                getFavoriteMovies(user.userId)
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _favoriteMovies.value = emptyList()
    }

    fun getFavoriteMovies(userId: String) {
        viewModelScope.launch {
            _favoriteMovies.value = repository.getFavoriteMovies(userId)
        }
    }

    fun addFavoriteMovie(movie: Movie) {
        viewModelScope.launch {
            val userId = currentUser.value?.userId ?: return@launch
            val favoriteMovie = FavoriteMovie(movie.id, userId, movie.title, movie.posterPath)
            repository.addFavoriteMovie(favoriteMovie)
            getFavoriteMovies(userId)
        }
    }

    fun removeFavoriteMovie(movieId: Int) {
        viewModelScope.launch {
            val userId = currentUser.value?.userId ?: return@launch
            val favoriteMovie = _favoriteMovies.value.find { it.movieId == movieId } ?: return@launch
            repository.removeFavoriteMovie(favoriteMovie)
            getFavoriteMovies(userId)
        }
    }
}