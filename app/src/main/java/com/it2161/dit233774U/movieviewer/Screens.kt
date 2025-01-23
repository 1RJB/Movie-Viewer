package com.it2161.dit233774U.movieviewer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@Composable
fun LoginScreen(viewModel: MovieViewModel, navController: NavController) {
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val currentUser by viewModel.currentUser.collectAsState()
    val loginError by viewModel.loginError.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            navController.navigate("movieList")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = userId,
            onValueChange = { userId = it },
            label = { Text("User ID") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.loginUser(userId, password)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isOffline
        ) {
            Text("Login")
        }
        loginError?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(
            onClick = { navController.navigate("register") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isOffline
        ) {
            Text("Register")
        }
        if (isOffline) {
            Text("Offline mode: Login and registration are unavailable", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun RegisterScreen(viewModel: MovieViewModel, navController: NavController) {
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var preferredName by remember { mutableStateOf("") }
    val isOffline by viewModel.isOffline.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = userId,
            onValueChange = { userId = it },
            label = { Text("User ID") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = preferredName,
            onValueChange = { preferredName = it },
            label = { Text("Preferred Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.registerUser(userId, password, preferredName)
                navController.navigate("login")
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isOffline
        ) {
            Text("Register")
        }
        if (isOffline) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Offline mode: Registration is unavailable", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun MovieListScreen(viewModel: MovieViewModel, navController: NavController) {
    val movies by viewModel.movies.collectAsState()
    var selectedCategory by remember { mutableStateOf("Popular") }
    val isOffline by viewModel.isOffline.collectAsState()
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedCategory, isOffline) {
        isLoading = true
        error = null
        try {
            when (selectedCategory) {
                "Popular" -> viewModel.getPopularMovies()
                "Top Rated" -> viewModel.getTopRatedMovies()
                "Now Playing" -> viewModel.getNowPlayingMovies()
                "Upcoming" -> viewModel.getUpcomingMovies()
            }
        } catch (e: Exception) {
            error = "Failed to load movies: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Popular", modifier = Modifier.clickable { selectedCategory = "Popular" })
            Text(text = "Top Rated", modifier = Modifier.clickable { selectedCategory = "Top Rated" })
            Text(text = "Now Playing", modifier = Modifier.clickable { selectedCategory = "Now Playing" })
            Text(text = "Upcoming", modifier = Modifier.clickable { selectedCategory = "Upcoming" })
        }
        if (isOffline) {
            Text("Offline mode: Showing cached movies", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.error)
        }
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Text(error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
            }
            movies.isEmpty() -> {
                Text("No movies found", modifier = Modifier.padding(16.dp))
            }
            else -> {
                LazyColumn {
                    items(movies) { movie ->
                        MovieItem(movie) {
                            navController.navigate("movieDetail/${movie.id}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MovieDetailScreen(viewModel: MovieViewModel, movieId: Int) {
    val movieDetails by viewModel.movieDetails.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()

    LaunchedEffect(movieId) {
        viewModel.getMovieDetails(movieId)
        if (!isOffline) {
            viewModel.getMovieReviews(movieId)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        movieDetails?.let { movie ->
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
            Text(text = movie.title, style = MaterialTheme.typography.headlineMedium)
            Text(text = "Release Date: ${movie.releaseDate}")
            Text(text = "Rating: ${movie.voteAverage}")
            Text(text = "Overview: ${movie.overview}")

            if (!isOffline) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Reviews", style = MaterialTheme.typography.titleLarge)
                LazyColumn {
                    items(reviews) { review ->
                        ReviewItem(review)
                    }
                }
            } else {
                Text("Reviews unavailable in offline mode", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = review.author, style = MaterialTheme.typography.titleMedium)
            Text(text = review.content, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ProfileScreen(viewModel: MovieViewModel, navController: NavController) {
    val currentUser by viewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        currentUser?.let { user ->
            Text("User ID: ${user.userId}", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Preferred Name: ${user.preferredName}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate("favorites") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Favorites")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    viewModel.logout()
                    navController.navigate("login")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        } ?: run {
            Text("Not logged in", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate("login") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Go to Login")
            }
        }
    }
}

@Composable
fun FavoriteMoviesScreen(viewModel: MovieViewModel, navController: NavController) {
    val favoriteMovies by viewModel.favoriteMovies.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Favorite Movies",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        LazyColumn {
            items(favoriteMovies) { movie ->
                MovieItem(
                    Movie(
                        id = movie.movieId,
                        title = movie.title,
                        posterPath = movie.posterPath,
                        releaseDate = "",
                        voteAverage = 0.0,
                        overview = "",
                        adult = false,
                        genres = emptyList(),
                        originalLanguage = "",
                        runtime = 0,
                        voteCount = 0,
                        revenue = 0
                    )
                ) {
                    navController.navigate("movieDetail/${movie.movieId}")
                }
            }
        }
    }
}

@Composable
fun SearchScreen(viewModel: MovieViewModel, navController: NavController) {
    val searchResults by viewModel.movies.collectAsState()
    var query by remember { mutableStateOf("") }
    val isOffline by viewModel.isOffline.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Search Movies", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search query") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                viewModel.searchMovies(query)
            },
            enabled = !isOffline
        ) {
            Text("Search")
        }
        if (isOffline) {
            Text("Offline mode: Searching in cached movies", color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(searchResults) { movie ->
                MovieItem(movie) {
                    navController.navigate("movieDetail/${movie.id}")
                }
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w185${movie.posterPath}",
            contentDescription = movie.title,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = movie.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = movie.releaseDate, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Rating: ${movie.voteAverage}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

