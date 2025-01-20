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
    var errorMessage by remember { mutableStateOf("") }
    val currentUser by viewModel.currentUser.collectAsState()

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
                if (userId.isNotEmpty() && password.isNotEmpty()) {
                    viewModel.loginUser(userId, password)
                } else {
                    errorMessage = "Please enter both User ID and Password"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(
            onClick = { navController.navigate("register") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun RegisterScreen(viewModel: MovieViewModel, navController: NavController) {
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var preferredName by remember { mutableStateOf("") }

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
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
    }
}

@Composable
fun MovieListScreen(viewModel: MovieViewModel, navController: NavController) {
    val movies by viewModel.movies.collectAsState()
    var selectedCategory by remember { mutableStateOf("Popular") }

    LaunchedEffect(selectedCategory) {
        when (selectedCategory) {
            "Popular" -> viewModel.getPopularMovies()
            "Top Rated" -> viewModel.getTopRatedMovies()
            "Now Playing" -> viewModel.getNowPlayingMovies()
            "Upcoming" -> viewModel.getUpcomingMovies()
        }
    }

    Column {
        // Category Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Popular", modifier = Modifier.clickable {
                selectedCategory = "Popular"
            })
            Text("Top Rated", modifier = Modifier.clickable {
                selectedCategory = "Top Rated"
            })
            Text("Now Playing", modifier = Modifier.clickable {
                selectedCategory = "Now Playing"
            })
            Text("Upcoming", modifier = Modifier.clickable {
                selectedCategory = "Upcoming"
            })
        }

        // Display Movies
        LazyColumn(modifier = Modifier.padding(top = 56.dp)) { // Added padding to move down from status bar
            items(movies) { movie ->
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
            Text(movie.title, style = MaterialTheme.typography.headlineSmall)
            Text(movie.releaseDate, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun MovieDetailScreen(viewModel: MovieViewModel, movieId: Int) {
    val movieDetails by viewModel.movieDetails.collectAsState()
    val reviews by viewModel.reviews.collectAsState()

    LaunchedEffect(movieId) {
        viewModel.getMovieDetails(movieId)
        viewModel.getMovieReviews(movieId)
    }

    movieDetails?.let { movie ->
        Column(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w342${movie.posterPath}",
                contentDescription = movie.title,
                modifier = Modifier.fillMaxWidth()
            )
            Text(movie.title, style = MaterialTheme.typography.headlineMedium)
            Text("Release Date: ${movie.releaseDate}")
            Text("Runtime: ${movie.runtime} minutes")
            Text("Vote Average: ${movie.voteAverage}")
            Text("Overview: ${movie.overview}")

            Spacer(modifier = Modifier.height(16.dp))
            Text("Reviews", style = MaterialTheme.typography.headlineSmall)
            LazyColumn {
                items(reviews) { review ->
                    ReviewItem(review)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    // Add to favorites
                    viewModel.addFavoriteMovie(movie)
                }
            ) {
                Text("Add to Favorites")
            }
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = "Author: ${review.author}", style = MaterialTheme.typography.bodyMedium)
        Text(text = review.content, style = MaterialTheme.typography.bodySmall)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
    }
}