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
                navController.navigate("movieList")
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

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Popular", modifier = Modifier.clickable {
                selectedCategory = "Popular"
                viewModel.getPopularMovies()
            })
            Text("Top Rated", modifier = Modifier.clickable {
                selectedCategory = "Top Rated"
                viewModel.getTopRatedMovies()
            })
            Text("Now Playing", modifier = Modifier.clickable {
                selectedCategory = "Now Playing"
                viewModel.getNowPlayingMovies()
            })
            Text("Upcoming", modifier = Modifier.clickable {
                selectedCategory = "Upcoming"
                viewModel.getUpcomingMovies()
            })
        }
        LazyColumn {
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
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(review.author, style = MaterialTheme.typography.titleMedium)
        Text(review.content)
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
            Text("User ID: ${user.userId}")
            Text("Preferred Name: ${user.preferredName}")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.logout()
                navController.navigate("login")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}

@Composable
fun FavoriteMoviesScreen(viewModel: MovieViewModel, navController: NavController) {
    val favoriteMovies by viewModel.favoriteMovies.collectAsState()

    LazyColumn {
        items(favoriteMovies) { movie ->
            MovieItem(Movie(movie.movieId, movie.title, "", movie.posterPath, "", 0.0, false, emptyList(), "", 0, 0, 0)) {
                navController.navigate("movieDetail/${movie.movieId}")
            }
        }
    }
}

@Composable
fun SearchScreen(viewModel: MovieViewModel, navController: NavController) {
    var query by remember { mutableStateOf("") }
    val movies by viewModel.movies.collectAsState()

    Column {
        TextField(
            value = query,
            onValueChange = {
                query = it
                viewModel.searchMovies(it)
            },
            label = { Text("Search Movies") },
            modifier = Modifier.fillMaxWidth()
        )
        LazyColumn {
            items(movies) { movie ->
                MovieItem(movie) {
                    navController.navigate("movieDetail/${movie.id}")
                }
            }
        }
    }
}