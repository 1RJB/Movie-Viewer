package com.it2161.dit233774U.movieviewer

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Repository(context: Context) {
    private val apiService: ApiService
    private val database: AppDatabase = AppDatabase.getDatabase(context)
    private val userDao: UserDao = database.userDao()
    private val favoriteMovieDao: FavoriteMovieDao = database.favoriteMovieDao()
    private val movieDao: MovieDao = database.movieDao()

    // Use Retrofit to build the service for The Movie DB
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    // Replace with a valid API key from The Movie DB
    private val apiKey = "531a735640057af99e002f9185093005"

    // ------------------- Movie API methods ------------------- //
    suspend fun getPopularMovies() = apiService.getPopularMovies(apiKey)
    suspend fun getTopRatedMovies() = apiService.getTopRatedMovies(apiKey)
    suspend fun getNowPlayingMovies() = apiService.getNowPlayingMovies(apiKey)
    suspend fun getUpcomingMovies() = apiService.getUpcomingMovies(apiKey)
    suspend fun getMovieDetails(movieId: Int) = apiService.getMovieDetails(movieId, apiKey)
    suspend fun getMovieReviews(movieId: Int) = apiService.getMovieReviews(movieId, apiKey)
    suspend fun searchMovies(query: String) = apiService.searchMovies(apiKey, query)
    suspend fun getSimilarMovies(movieId: Int) = apiService.getSimilarMovies(movieId, apiKey)

    // ------------------- User Auth methods ------------------- //
    suspend fun registerUser(user: User) = withContext(Dispatchers.IO) {
        userDao.insertUser(user)
    }

    suspend fun loginUser(userId: String, password: String): User? = withContext(Dispatchers.IO) {
        val user = userDao.getUser(userId)
        if (user?.password == password) user else null
    }

    // ------------------- Favorite Movies ------------------- //
    suspend fun getFavoriteMovies(userId: String): List<FavoriteMovie> = withContext(Dispatchers.IO) {
        favoriteMovieDao.getFavoriteMovies(userId)
    }

    suspend fun addFavoriteMovie(favoriteMovie: FavoriteMovie) = withContext(Dispatchers.IO) {
        favoriteMovieDao.insertFavoriteMovie(favoriteMovie)
    }

    suspend fun removeFavoriteMovie(favoriteMovie: FavoriteMovie) = withContext(Dispatchers.IO) {
        favoriteMovieDao.deleteFavoriteMovie(favoriteMovie)
    }

    // ------------------- Caching ------------------- //
    suspend fun cacheMovies(movies: List<Movie>) = withContext(Dispatchers.IO) {
        movieDao.insertMovies(movies)
    }

    suspend fun getCachedMovies(): List<Movie> = withContext(Dispatchers.IO) {
        movieDao.getAllMovies()
    }

    suspend fun getCachedMovieDetails(movieId: Int): Movie? = withContext(Dispatchers.IO) {
        movieDao.getMovieById(movieId)
    }

    suspend fun searchCachedMovies(query: String): List<Movie> = withContext(Dispatchers.IO) {
        movieDao.searchMovies("%$query%")
    }
}

