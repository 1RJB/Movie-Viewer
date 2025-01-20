package com.it2161.dit233774U.movieviewer

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun Navigation(viewModel: MovieViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(viewModel, navController)
        }
        composable("register") {
            RegisterScreen(viewModel, navController)
        }
        composable("movieList") {
            MovieListScreen(viewModel, navController)
        }
        composable(
            "movieDetail/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
            MovieDetailScreen(viewModel, movieId)
        }
        composable("profile") {
            ProfileScreen(viewModel, navController)
        }
        composable("favorites") {
            FavoriteMoviesScreen(viewModel, navController)
        }
        composable("search") {
            SearchScreen(viewModel, navController)
        }
    }
}