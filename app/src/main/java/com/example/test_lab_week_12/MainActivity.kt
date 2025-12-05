package com.example.test_lab_week_12

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.example.test_lab_week_12.model.Movie
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var movieAdapter: MovieAdapter
    private lateinit var recyclerView: RecyclerView

    private val movieViewModel: MovieViewModel by viewModels {
        MovieViewModelFactory(
            (application as MovieApplication).movieRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.movie_list)
        movieAdapter = MovieAdapter(object : MovieAdapter.MovieClickListener {
            override fun onMovieClick(movie: Movie) {
                val intent = Intent(this@MainActivity, DetailsActivity::class.java).apply {
                    putExtra(DetailsActivity.EXTRA_TITLE, movie.title)
                    putExtra(DetailsActivity.EXTRA_RELEASE, movie.release_date)
                    putExtra(DetailsActivity.EXTRA_OVERVIEW, movie.overview)
                    putExtra(DetailsActivity.EXTRA_POSTER, movie.poster_path)
                }
                startActivity(intent)
            }
        })
        recyclerView.adapter = movieAdapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    movieViewModel.popularMovies.collect { movies ->
                        movieAdapter.addMovies(movies)
                    }
                }

                launch {
                    movieViewModel.error.collect { error ->
                        if (error.isNotEmpty()) {
                            Snackbar
                                .make(
                                    recyclerView, error, Snackbar.LENGTH_LONG
                                ).show()
                        }
                    }
                }
            }
        }
    }
}

class MovieViewModelFactory(
    private val movieRepository: MovieRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
            return MovieViewModel(movieRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}