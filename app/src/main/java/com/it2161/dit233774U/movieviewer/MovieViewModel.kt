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

    // Favorite movies
    private val _favoriteMovies = MutableStateFlow<List<FavoriteMovie>>(emptyList())
    val favoriteMovies: StateFlow<List<FavoriteMovie>> = _favoriteMovies

    // Currently logged-in user
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    // Error handling for login
    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    // Network status
    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline

    // ------------------- Movie fetching (TMDB) ------------------- //
    fun getPopularMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getPopularMovies()
                _movies.value = response.results
                repository.cacheMovies(response.results)
            } catch (e: Exception) {
                if (_isOffline.value) {
                    _movies.value = repository.getCachedMovies()
                }
            }
        }
    }

    fun getTopRatedMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getTopRatedMovies()
                _movies.value = response.results
                repository.cacheMovies(response.results)
            } catch (e: Exception) {
                if (_isOffline.value) {
                    _movies.value = repository.getCachedMovies()
                }
            }
        }
    }

    fun getNowPlayingMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getNowPlayingMovies()
                _movies.value = response.results
                repository.cacheMovies(response.results)
            } catch (e: Exception) {
                if (_isOffline.value) {
                    _movies.value = repository.getCachedMovies()
                }
            }
        }
    }

    fun getUpcomingMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getUpcomingMovies()
                _movies.value = response.results
                repository.cacheMovies(response.results)
            } catch (e: Exception) {
                if (_isOffline.value) {
                    _movies.value = repository.getCachedMovies()
                }
            }
        }
    }

    fun getMovieDetails(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _movieDetails.value = repository.getMovieDetails(movieId)
            } catch (e: Exception) {
                if (_isOffline.value) {
                    _movieDetails.value = repository.getCachedMovieDetails(movieId)
                }
            }
        }
    }

    fun getMovieReviews(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getMovieReviews(movieId)
                _reviews.value = response.results
            } catch (e: Exception) {
                // Handle error or set empty list if offline
                _reviews.value = emptyList()
            }
        }
    }

    fun searchMovies(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.searchMovies(query)
                _movies.value = response.results
            } catch (e: Exception) {
                // Handle error or search in cached movies if offline
                if (_isOffline.value) {
                    _movies.value = repository.searchCachedMovies(query)
                }
            }
        }
    }

    fun getSimilarMovies(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getSimilarMovies(movieId)
                _movies.value = response.results
            } catch (e: Exception) {
                // Handle error or set empty list if offline
                _movies.value = emptyList()
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
            val favoriteMovie = FavoriteMovie(movie.id, userId, movie.title, movie.posterPath)
            repository.addFavoriteMovie(favoriteMovie)
            getFavoriteMovies(userId)
        }
    }

    fun removeFavoriteMovie(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = currentUser.value?.userId ?: return@launch
            val favoriteMovie = _favoriteMovies.value.find { it.movieId == movieId } ?: return@launch
            repository.removeFavoriteMovie(favoriteMovie)
            getFavoriteMovies(userId)
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