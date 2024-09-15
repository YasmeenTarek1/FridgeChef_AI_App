package com.example.recipeapp.ui.feedFragment

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recipeapp.Repository

class FeedViewModelFactory(private val application: Application , private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            return FeedViewModel(application , repository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
