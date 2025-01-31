package com.it2161.dit233774U.movieviewer

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository(application)

    // Movie lists and details
    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _movieDetails = MutableStateFlow<Movie?>(null)
    val movieDetails: StateFlow<Movie?> = _movieDetails

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    // Similar Movies
    private val _similarMovies = MutableStateFlow<List<Movie>>(emptyList())
    val similarMovies: StateFlow<List<Movie>> = _similarMovies

    // Favorite movies
    private val _favoriteMovies = MutableStateFlow<List<FavoriteMovie>>(emptyList())
    val favoriteMovies: StateFlow<List<FavoriteMovie>> = _favoriteMovies

    // Favorite movie check
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    // Currently logged-in user
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    // Error handling for login
    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    // Network status
    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // ------------------- Movie fetching (TMDB) ------------------- //
    private fun fetchMovies(fetchFunction: suspend () -> List<Movie>) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val fetchedMovies = fetchFunction()
                _movies.value = fetchedMovies
                repository.cacheMovies(fetchedMovies)
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error fetching movies", e)
                if (_isOffline.value) {
                    _movies.value = repository.getCachedMovies()
                } else {
                    _error.value = "Failed to load movies: ${e.message}"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getPopularMovies() = fetchMovies { repository.getPopularMovies() }
    fun getTopRatedMovies() = fetchMovies { repository.getTopRatedMovies() }
    fun getNowPlayingMovies() = fetchMovies { repository.getNowPlayingMovies() }
    fun getUpcomingMovies() = fetchMovies { repository.getUpcomingMovies() }

    fun getMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _movieDetails.value = repository.getMovieDetails(movieId)
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error fetching movie details", e)
                if (_isOffline.value) {
                    _movieDetails.value = repository.getCachedMovieDetails(movieId)
                } else {
                    _error.value = "Failed to load movie details: ${e.message}"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getMovieReviews(movieId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = repository.getMovieReviews(movieId)
                _reviews.value = response.results
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error fetching movie reviews", e)
                _error.value = "Failed to load reviews: ${e.message}"
                _reviews.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchMovies(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = repository.searchMovies(query)
                _movies.value = response.results
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error searching movies", e)
                if (_isOffline.value) {
                    _movies.value = repository.searchCachedMovies(query)
                } else {
                    _error.value = "Failed to search movies: ${e.message}"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getSimilarMovies(movieId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = repository.getSimilarMovies(movieId)
                _similarMovies.value = response.results
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error fetching similar movies", e)
                _error.value = "Failed to load similar movies: ${e.message}"
                _similarMovies.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ------------------- User Auth ------------------- //
    fun registerUser(userId: String, password: String, preferredName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = User(userId, password, preferredName)
                repository.registerUser(user)
                _loginError.value = null
            } catch (e: Exception) {
                _loginError.value = "Registration failed: ${e.message}"
            }
        }
    }

    fun loginUser(userId: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("MovieViewModel", "Attempting to login user: $userId")
                val user = repository.loginUser(userId, password)
                if (user != null) {
                    Log.d("MovieViewModel", "Login successful for user: $userId")
                    _currentUser.value = user
                    getFavoriteMovies(user.userId)
                    _loginError.value = null
                } else {
                    Log.d("MovieViewModel", "Login failed: Invalid username or password")
                    _loginError.value = "Invalid username or password"
                }
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Login failed with exception", e)
                _loginError.value = "Login failed: ${e.message}"
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _favoriteMovies.value = emptyList()
    }

    // ------------------- Favorites ------------------- //
    fun getFavoriteMovies(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _favoriteMovies.value = repository.getFavoriteMovies(userId)
        }
    }

    fun addFavoriteMovie(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = currentUser.value?.userId ?: return@launch
            val favoriteMovie = FavoriteMovie(movie.id, userId, movie.title,
                movie.poster_path.toString()
            )
            repository.addFavoriteMovie(favoriteMovie)
            _isFavorite.value = true
        }
    }

    fun removeFavoriteMovie(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = currentUser.value?.userId ?: return@launch
            val favoriteMovie = _favoriteMovies.value.find { it.movieId == movieId } ?: return@launch
            repository.removeFavoriteMovie(favoriteMovie)
            _isFavorite.value = false
            getFavoriteMovies(userId)
        }
    }

    fun checkIfFavorite(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = currentUser.value?.userId ?: return@launch
            _isFavorite.value = repository.isFavoriteMovie(userId, movieId)
        }
    }

    // ------------------- Offline Mode ------------------- //
    fun setOfflineMode(offline: Boolean) {
        _isOffline.value = offline
        if (offline) {
            // Load cached data when going offline
            viewModelScope.launch(Dispatchers.IO) {
                _movies.value = repository.getCachedMovies()
            }
        }
    }
}

