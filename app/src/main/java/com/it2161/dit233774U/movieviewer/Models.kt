package com.it2161.dit233774U.movieviewer

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String?,
    val poster_path: String?,
    val release_date: String?,
    val vote_average: Double?,
    val adult: Boolean?,
    @TypeConverters(Converters::class) val genres: List<Genre>?,
    val original_language: String?,
    val runtime: Int?,
    val vote_count: Int?,
    val revenue: Long?
)

data class Genre(
    val id: Int = 0,
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
    val overview: String,
    val poster_path: String,
    val release_date: String,
    val vote_average: Double,
    val adult: Boolean,
    @TypeConverters(Converters::class) val genres: List<Genre>,
    val original_language: String?,
    val runtime: Int?,
    val vote_count: Int,
    val revenue: Long
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

    @TypeConverter
    fun fromGenreList(genres: List<Genre>?): String? {
        if (genres == null) return null
        return Gson().toJson(genres)
    }

    @TypeConverter
    fun toGenreList(value: String?): List<Genre>? {
        if (value == null) return null
        val listType = object : TypeToken<List<Genre>>() {}.type
        return Gson().fromJson(value, listType)
    }
}
