package com.example.test_lab_week_12

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test_lab_week_12.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Calendar

class MovieViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    private val _popularMovies = MutableStateFlow<List<Movie>>(
        emptyList()
    )
    val popularMovies: StateFlow<List<Movie>> = _popularMovies

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    init {
        fetchPopularMovies()
    }

    private fun fetchPopularMovies() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()

        viewModelScope.launch(Dispatchers.IO) {
            movieRepository.fetchMovies()
                .map { popularMovies ->
                    popularMovies
                        .filter { movie ->
                            movie.release_date?.startsWith(currentYear) == true
                        }
                        .sortedByDescending { it.popularity }
                }
                .catch { exception ->
                    _error.value = "An exception occurred: ${exception.message}"
                }
                .collect { moviesList ->
                    _popularMovies.value = moviesList
                }
        }
    }
}