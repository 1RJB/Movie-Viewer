package com.it2161.dit233774U.movieviewer

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MovieViewModel
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate started")
        // Create ViewModel using the factory
        viewModel = ViewModelProvider(this, MovieViewModelFactory(application))[MovieViewModel::class.java]

        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Set up network callback
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                viewModel.setOfflineMode(false)
            }

            override fun onLost(network: Network) {
                viewModel.setOfflineMode(true)
            }
        }

        // Register network callback
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        Log.d("MainActivity", "onCreate called")

        setContent {
            Log.d("MainActivity", "setContent called")
            MovieViewerTheme {
                // Use a Scaffold to help manage top bars and offset from the status bar
                Scaffold { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Log.d("MainActivity", "Surface Composable")
                        val viewModel: MovieViewModel = viewModel(factory = MovieViewModelFactory(application))
                        Navigation(viewModel)
                        Log.d("MainActivity", "Navigation Composable")
                    }
                }
            }
        }
        Log.d("MainActivity", "onCreate completed")
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister network callback
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}