package com.it2161.dit233774U.movieviewer

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MovieViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d("ViewModelFactory", "Creating ViewModel: ${modelClass.simpleName}")
        if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val viewModel = MovieViewModel(application)
            Log.d("ViewModelFactory", "ViewModel created successfully: $viewModel")
            return viewModel as T
        }
        Log.e("ViewModelFactory", "Unknown ViewModel class: ${modelClass.simpleName}")
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}