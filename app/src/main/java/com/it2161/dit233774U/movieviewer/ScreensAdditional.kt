package com.it2161.dit233774U.movieviewer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@Composable
fun ProfileScreen(viewModel: MovieViewModel, navController: NavController) {
    val user by viewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        user?.let {
            Text(text = "User ID: ${it.userId}")
            Text(text = "Preferred Name: ${it.preferredName}")
        } ?: run {
            Text(text = "No user logged in.")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("movieList") }) {
            Text("Go to Movie List")
        }
    }
}

@Composable
fun FavoriteMoviesScreen(viewModel: MovieViewModel, navController: NavController) {
    val favoriteMovies by viewModel.favoriteMovies.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadFavoriteMovies()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Your Favorite Movies")
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(favoriteMovies) { fav ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // Navigate to detail using movieId
                            navController.navigate("movieDetail/${fav.movieId}")
                        }
                        .padding(vertical = 8.dp)
                ) {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w185${fav.posterPath}",
                        contentDescription = fav.title,
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(fav.title)
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(onClick = {
                            viewModel.removeFavoriteMovie(fav)
                        }) {
                            Text("Remove")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchScreen(viewModel: MovieViewModel, navController: NavController) {
    val searchResults by viewModel.searchResults.collectAsState()
    var query by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Search Movies")
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Movie Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            viewModel.searchMovies(query)
        }) {
            Text("Search")
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(searchResults) { movie ->
                MovieItem(movie = movie) {
                    navController.navigate("movieDetail/${movie.id}")
                }
            }
        }
    }
}