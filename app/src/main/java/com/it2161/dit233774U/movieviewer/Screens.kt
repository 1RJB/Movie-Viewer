package com.it2161.dit233774U.movieviewer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(viewModel: MovieViewModel, navController: NavController) {
    val movies by viewModel.movies.collectAsState()
    var selectedCategory by remember { mutableStateOf("Popular") }
    val isOffline by viewModel.isOffline.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(selectedCategory, isOffline) {
        when (selectedCategory) {
            "Popular" -> viewModel.getPopularMovies()
            "Top Rated" -> viewModel.getTopRatedMovies()
            "Now Playing" -> viewModel.getNowPlayingMovies()
            "Upcoming" -> viewModel.getUpcomingMovies()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movies") },
                actions = {
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                    }
                    IconButton(onClick = { navController.navigate("search") }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { CategoryButton("Popular", selectedCategory) { selectedCategory = "Popular" } }
                item { CategoryButton("Top Rated", selectedCategory) { selectedCategory = "Top Rated" } }
                item { CategoryButton("Now Playing", selectedCategory) { selectedCategory = "Now Playing" } }
                item { CategoryButton("Upcoming", selectedCategory) { selectedCategory = "Upcoming" } }
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
}

@Composable
fun CategoryButton(category: String, selectedCategory: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (category == selectedCategory) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
        )
    ) {
        Text(category)
    }
}

@Composable
fun MovieDetailScreen(viewModel: MovieViewModel, movieId: Int, navController: NavController) {
    val movieDetails by viewModel.movieDetails.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val similarMovies by viewModel.similarMovies.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    LaunchedEffect(movieId) {
        viewModel.getMovieDetails(movieId)
        if (!isOffline) {
            viewModel.getMovieReviews(movieId)
            viewModel.getSimilarMovies(movieId)
        }
        viewModel.checkIfFavorite(movieId)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        movieDetails?.let { movie ->
            AsyncImage(
                model = movie.poster_path?.let { "https://image.tmdb.org/t/p/w500$it" },
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
            Text(text = movie.title, style = MaterialTheme.typography.headlineMedium)
            movie.release_date?.let { Text(text = "Release Date: $it") }
            movie.vote_average?.let { Text(text = "Vote Average: $it") }
            movie.adult?.let { Text(text = "Adult: $it") }
            movie.runtime?.let { Text(text = "Runtime: $it minutes") }
            movie.vote_count?.let { Text(text = "Vote Count: $it") }
            movie.revenue?.let { Text(text = "Revenue: $it") }
            movie.overview?.let { Text(text = "Overview: $it") }
            movie.genres?.let { genres ->
                Text(text = "Genres: ${genres.joinToString { it.name }}")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (isFavorite) {
                        viewModel.removeFavoriteMovie(movie.id)
                    } else {
                        viewModel.addFavoriteMovie(movie)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isFavorite) "Remove from Favorites" else "Add to Favorites")
            }

            if (!isOffline) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Reviews", style = MaterialTheme.typography.titleLarge)
                // Use a Column instead of LazyColumn
                Column {
                    reviews.forEach { review ->
                        ReviewItem(review)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Similar Movies", style = MaterialTheme.typography.titleLarge)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(similarMovies) { similarMovie ->
                        MovieItem(similarMovie) {
                            navController.navigate("movieDetail/${similarMovie.id}")
                        }
                    }
                }
            } else {
                Text("Reviews and similar movies unavailable in offline mode", color = MaterialTheme.colorScheme.error)
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
                        poster_path = movie.poster_path,
                        release_date = movie.release_date,
                        vote_average = movie.vote_average,
                        overview = movie.overview,
                        adult = movie.adult,
                        genres = movie.genres,
                        original_language = movie.original_language,
                        runtime = movie.runtime,
                        vote_count = movie.vote_count,
                        revenue = movie.revenue
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
        // Construct the full image URL
        val imageUrl = if (movie.poster_path != null) {
            "https://image.tmdb.org/t/p/w500/${movie.poster_path}"
        } else {
            R.drawable.placeholder  // Use placeholder if posterPath is null
        }

        AsyncImage(
            model = imageUrl,
            contentDescription = movie.title,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = movie.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = movie.release_date ?: "Release date unknown", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Vote Average: ${movie.vote_average?.toString() ?: "N/A"}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

