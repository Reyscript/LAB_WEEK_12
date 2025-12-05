# LAB\_WEEK\_12 - Coroutines, Kotlin Flow & Reactive Data Streaming

**Link Google Drive**
[Keseluruhan Project](https://drive.google.com/drive/u/5/folders/1yeMcxHG5oVml6V4A671B3dryGH5Gy68c)
[Images & Screenshots](https://drive.google.com/drive/u/5/folders/1iBa7v4cERxJ-YAy5r4K2vSfv27E7XMAs)
[APK File](https://drive.google.com/drive/u/5/folders/1bhmekM6DSfghtICGs3nwkOW9XcXuEqGX)

## Commit History

  - **Commit No. 1** - Initial setup (Retrofit, Data Models, App Structure)
  - **Commit No. 2** - Building app with Coroutines and LiveData (Initial API fetch, Image Loading with Glide, Detail Intent Implementation)
  - **Commit No. 3** - Upgrading app with Flow (Migrate LiveData to Kotlin Flow and StateFlow, use `lifecycleScope.repeatOnLifecycle`)
  - **Commit No. 4** - Assignment (Implement data filtering and sorting using Flow `map` operator)

-----

## Fitur Aplikasi

### **Coroutines & Asynchronous Programming**

  - **Non-Blocking Network Calls** - Menggunakan Kotlin Coroutines (`suspend fun`) untuk memanggil API secara asynchronous.
  - **Thread Safety** - Menggunakan `Dispatchers.IO` di Repository untuk operasi network dan `viewModelScope` di ViewModel.
  - **Structured Concurrency** - Memastikan operasi berjalan dalam scope yang terstruktur (`viewModelScope`) untuk manajemen *lifecycle* yang aman.

### **Kotlin Flow & Reactive UI**

  - **State Management** - Mengganti `LiveData` dengan **`StateFlow`** untuk menampung *state* data dan error.
  - **Lifecycle Awareness** - Menggunakan `lifecycleScope.repeatOnLifecycle(STARTED)` di Activity untuk secara aman mengumpulkan (`collect`) emisi dari Flow.
  - **Declarative Data Transformation** - Menggunakan operator Flow (`map`, `filter`, `sortedByDescending`) di ViewModel untuk memproses data.

### **API & Data Handling**

  - **Data Filtering (Commit 4)** - Menerapkan filter agar hanya menampilkan film yang rilis di **tahun berjalan**.
  - **Data Sorting (Commit 4)** - Menerapkan sorting berdasarkan **popularitas** secara menurun.
  - **Image Loading** - Memuat poster film dari URL menggunakan library **Glide**.
  - **Navigation** - Mengirim data film melalui `Intent` untuk ditampilkan di `DetailsActivity`.

-----

## Teknologi yang Digunakan

### **Android Architecture Components**

  - **ViewModel** - *Data holder* yang *lifecycle-aware*.
  - **Kotlin Coroutines** - Pustaka untuk *asynchronous programming* yang terstruktur.
  - **Kotlin Flow & StateFlow** - *Reactive data streams* untuk *state management* yang modern.
  - **Lifecycle Awareness** - Menggunakan `repeatOnLifecycle` untuk manajemen Flow yang aman.

### **Networking & Data Processing**

  - **Retrofit** - *Type-safe HTTP client* untuk komunikasi REST API (The Movie DB).
  - **Gson Converter** - Digunakan oleh Retrofit untuk konversi JSON ke Kotlin Data Classes.
  - **Glide** - Pustaka untuk memuat, menampilkan, dan *caching* gambar secara efisien.

### **UI Components**

  - **Activity** - `MainActivity` dan `DetailsActivity`.
  - **RecyclerView** - Menampilkan daftar film dalam format grid.
  - **Snackbar** - Menampilkan pesan *error* atau notifikasi.

-----

## Struktur Data & Components

### **Data Models**

```kotlin
data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String?,
    val release_date: String?,
    val popularity: Double,
    // ...
)
```

### **MovieRepository.kt (Flow Implementation)**

```kotlin
class MovieRepository(private val movieService: MovieService) {
    fun fetchMovies(): Flow<List<Movie>> {
        return flow {
            val popularMoviesResponse = movieService.getPopularMovies(apiKey)
            emit(popularMoviesResponse.results)
        }.flowOn(Dispatchers.IO)
    }
}
```

### **MovieViewModel.kt (StateFlow)**

```kotlin
class MovieViewModel(...) : ViewModel() {
    
    private val _popularMovies = MutableStateFlow<List<Movie>>(emptyList())
    val popularMovies: StateFlow<List<Movie>> = _popularMovies // StateFlow

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error // StateFlow

    // ...
}
```

-----

## Key Features Implementation

### **Flow Collection (MainActivity)**

Mengganti `LiveData.observe` dengan *collecting* `StateFlow` secara *lifecycle-aware*.

```kotlin
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        launch {
            movieViewModel.popularMovies.collect { movies ->
                movieAdapter.addMovies(movies)
            }
        }
        // ... launch untuk error.collect
    }
}
```

### **Flow Data Transformation (Commit 4 - MovieViewModel)**

Menerapkan *filtering* dan *sorting* menggunakan operator `map` pada Flow.

```kotlin
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
```

### **Intent Implementation (Commit 2 - MainActivity)**

Logika klik untuk navigasi ke halaman detail.

```kotlin
movieAdapter = MovieAdapter(object : MovieAdapter.MovieClickListener {
    override fun onMovieClick(movie: Movie) {
        val intent = Intent(this@MainActivity, DetailsActivity::class.java).apply {
            putExtra(DetailsActivity.EXTRA_TITLE, movie.title)
            putExtra(DetailsActivity.EXTRA_POSTER, movie.poster_path)
            // ... putExtra untuk data lainnya
        }
        startActivity(intent)
    }
})
```

-----

## Architecture Benefits

### **Modern Concurrency**

  - **Sequential Code** - Menggunakan Coroutines membuat kode asynchronous terlihat sequential dan lebih mudah dibaca daripada *callbacks*.
  - **Structured Resource Management** - Coroutines memastikan *network call* otomatis dibatalkan ketika ViewModel dihancurkan.

### **Reactive Stream Handling**

  - **Flexibility** - Flow menyediakan berbagai operator (`map`, `filter`, `combine`) yang jauh lebih fleksibel daripada `LiveData`.
  - **Testability** - Logic data processing (filter/sort) dipindahkan ke ViewModel/Repository, membuat Activity/Fragment lebih sederhana dan logic lebih mudah diuji secara independen.

### **Improved Performance**

  - **Efficient UI Update** - Menggunakan `repeatOnLifecycle(STARTED)` untuk menjamin *collection* Flow hanya aktif saat UI terlihat, menghemat CPU dan baterai.
  - **Clear Responsibility** - Pemisahan tugas yang bersih: Repository mengambil data, ViewModel memproses data, Activity menampilkan data.
