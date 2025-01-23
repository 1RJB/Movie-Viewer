package com.it2161.dit233774U.movieviewer

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String,
    val releaseDate: String,
    val voteAverage: Double,
    val adult: Boolean,
    val genres: List<Genre>,
    val originalLanguage: String,
    val runtime: Int,
    val voteCount: Int,
    val revenue: Long
)

data class Genre(
    val id: Int,
    val name: String
)

data class Review(
    val id: String,
    val author: String,
    val content: String,
    val createdAt: String
)

@Entity(tableName = "users")
data class User(
    @PrimaryKey val userId: String,
    val password: String,
    val preferredName: String
)

@Entity(tableName = "favorite_movies")
data class FavoriteMovie(
    @PrimaryKey val movieId: Int,
    val userId: String,
    val title: String,
    val posterPath: String
)

class Converters {
    @TypeConverter
    fun fromGenreList(value: List<Genre>): String {
        val gson = Gson()
        val type = object : TypeToken<List<Genre>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toGenreList(value: String): List<Genre> {
        val gson = Gson()
        val type = object : TypeToken<List<Genre>>() {}.type
        return gson.fromJson(value, type)
    }
}

