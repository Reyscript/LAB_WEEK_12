package com.example.test_lab_week_12.model

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String?,
    val release_date: String?,
    val popularity: Double,
    val vote_average: Double,
    val vote_count: Int
)