package com.it2161.dit233774U.movieviewer

   import MovieViewModelFactory
   import android.os.Bundle
   import android.util.Log
   import androidx.activity.ComponentActivity
   import androidx.activity.compose.setContent
   import androidx.compose.foundation.layout.fillMaxSize
   import androidx.compose.material3.MaterialTheme
   import androidx.compose.material3.Surface
   import androidx.compose.ui.Modifier
   import androidx.lifecycle.viewmodel.compose.viewModel

   class MainActivity : ComponentActivity() {
       override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           Log.d("MainActivity", "onCreate called")
           setContent {
               Log.d("MainActivity", "setContent called")
               MaterialTheme {
                   Surface(
                       modifier = Modifier.fillMaxSize(),
                       color = MaterialTheme.colorScheme.background
                   ) {
                       Log.d("MainActivity", "Surface Composable")
                       val viewModel: MovieViewModel = viewModel(
                           factory = MovieViewModelFactory(application)
                       )
                       Log.d("MainActivity", "ViewModel created: $viewModel")
                       Navigation(viewModel)
                       Log.d("MainActivity", "Navigation Composable")
                   }
               }
           }
       }
   }