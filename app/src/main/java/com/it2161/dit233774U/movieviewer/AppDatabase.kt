package com.it2161.dit233774U.movieviewer

import android.content.Context
import androidx.room.*

@Database(entities = [User::class, FavoriteMovie::class, Movie::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun favoriteMovieDao(): FavoriteMovieDao
    abstract fun movieDao(): MovieDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "movie_viewer_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUser(userId: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)
}

@Dao
interface FavoriteMovieDao {
    @Query("SELECT * FROM favorite_movies WHERE userId = :userId")
    suspend fun getFavoriteMovies(userId: String): List<FavoriteMovie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteMovie(favoriteMovie: FavoriteMovie)

    @Delete
    suspend fun deleteFavoriteMovie(favoriteMovie: FavoriteMovie)

    @Query("SELECT COUNT(*) FROM favorite_movies WHERE userId = :userId AND movieId = :movieId")
    suspend fun isFavoriteMovie(userId: String, movieId: Int): Int
}

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies")
    suspend fun getAllMovies(): List<Movie>

    @Query("SELECT * FROM movies WHERE id = :movieId")
    suspend fun getMovieById(movieId: Int): Movie?

    @Query("SELECT * FROM movies WHERE title LIKE :query")
    suspend fun searchMovies(query: String): List<Movie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<Movie>)
}

