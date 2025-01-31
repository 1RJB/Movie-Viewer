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
    val overview: String?,
    val posterPath: String?,
    val releaseDate: String?,
    val voteAverage: Double?,
    val adult: Boolean?,
    val genreIds: List<Int>?,
    val originalLanguage: String?,
    val runtime: Int?,
    val voteCount: Int?,
    val revenue: Long?
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
    fun fromIntList(value: List<Int>?): String? {
        if (value == null) return null
        return value.joinToString(",")
    }

    @TypeConverter
    fun toIntList(value: String?): List<Int>? {
        if (value == null) return null
        if (value.isEmpty()) return emptyList()
        return value.split(",").map { it.toInt() }
    }
}

