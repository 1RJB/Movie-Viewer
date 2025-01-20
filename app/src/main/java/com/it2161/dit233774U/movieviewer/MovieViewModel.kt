package com.it2161.dit233774U.movieviewer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository(application.applicationContext)

    // Keep track of currently logged-in user
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> get() = _currentUser

    // Movies displayed on the MovieListScreen
    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> get() = _movies

    // Detailed info for the selected movie
    private val _movieDetails = MutableStateFlow<Movie?>(null)
    val movieDetails: StateFlow<Movie?> get() = _movieDetails

    // Movie reviews
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> get() = _reviews

    // Search results
    private val _searchResults = MutableStateFlow<List<Movie>>(emptyList())
    val searchResults: StateFlow<List<Movie>> get() = _searchResults

    // Favorite movies
    private val _favoriteMovies = MutableStateFlow<List<FavoriteMovie>>(emptyList())
    val favoriteMovies: StateFlow<List<FavoriteMovie>> get() = _favoriteMovies

    // ------------------- Authentication ------------------- //
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
        }
    }

    // ------------------- Movie Lists ------------------- //
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

    // ------------------- Movie Details ------------------- //
    fun getMovieDetails(movieId: Int) {
        viewModelScope.launch {
            val movie = repository.getMovieDetails(movieId)
            _movieDetails.value = movie
        }
    }

    fun getMovieReviews(movieId: Int) {
        viewModelScope.launch {
            val reviewResponse = repository.getMovieReviews(movieId)
            _reviews.value = reviewResponse.results
        }
    }

    // ------------------- Similar Movies (Optional usage) ------------------- //
    fun getSimilarMovies(movieId: Int) {
        viewModelScope.launch {
            val response = repository.getSimilarMovies(movieId)
            _movies.value = response.results
        }
    }

    // ------------------- Search ------------------- //
    fun searchMovies(query: String) {
        viewModelScope.launch {
            val response = repository.searchMovies(query)
            _searchResults.value = response.results
        }
    }

    // ------------------- Favorites ------------------- //
    fun loadFavoriteMovies() {
        viewModelScope.launch {
            currentUser.value?.let { user ->
                val favs = repository.getFavoriteMovies(user.userId)
                _favoriteMovies.value = favs
            }
        }
    }

    fun addFavoriteMovie(movie: Movie) {
        viewModelScope.launch {
            currentUser.value?.let { user ->
                val favoriteMovie = FavoriteMovie(
                    movieId = movie.id,
                    userId = user.userId,
                    title = movie.title,
                    posterPath = movie.posterPath
                )
                repository.addFavoriteMovie(favoriteMovie)
                loadFavoriteMovies()
            }
        }
    }

    fun removeFavoriteMovie(favoriteMovie: FavoriteMovie) {
        viewModelScope.launch {
            repository.removeFavoriteMovie(favoriteMovie)
            loadFavoriteMovies()
        }
    }
}