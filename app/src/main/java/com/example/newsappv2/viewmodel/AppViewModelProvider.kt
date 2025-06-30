package com.example.newsappv2.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.newsappv2.NewsApplication

object AppViewModelProvider {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            HomeViewModel(
                newsApplication().container.newsRepository,
                newsApplication().userPreferencesDataStore
            )
        }

        initializer {
            CategoryViewModel(
                newsApplication().container.newsRepository,
                newsApplication().userPreferencesDataStore
            )
        }
    }
}

fun CreationExtras.newsApplication(): NewsApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as NewsApplication)