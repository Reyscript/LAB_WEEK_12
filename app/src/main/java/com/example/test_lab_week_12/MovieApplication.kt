package com.example.test_lab_week_12

import android.app.Application
import com.example.test_lab_week_12.api.MovieService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MovieApplication : Application() {
    lateinit var movieRepository: MovieRepository

    override fun onCreate() {
        super.onCreate()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create()) // ✅ Tetap gunakan Gson
            .build()

        val movieService = retrofit.create(MovieService::class.java)
        movieRepository = MovieRepository(movieService)
    }
}