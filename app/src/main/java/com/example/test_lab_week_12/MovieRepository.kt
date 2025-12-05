package com.example.test_lab_week_12

import com.example.test_lab_week_12.api.MovieService
import com.example.test_lab_week_12.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MovieRepository(private val movieService: MovieService) {
    private val apiKey = "f30450bc0fa5d315bec30efd37c7a11a"

    fun fetchMovies(): Flow<List<Movie>> {
        return flow {
            val popularMoviesResponse = movieService.getPopularMovies(apiKey)
            emit(popularMoviesResponse.results)
        }.flowOn(Dispatchers.IO)
    }
}