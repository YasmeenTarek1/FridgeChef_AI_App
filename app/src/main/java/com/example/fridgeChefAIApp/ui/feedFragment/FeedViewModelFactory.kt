package com.example.fridgeChefAIApp.ui.feedFragment

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fridgeChefAIApp.Repository

class FeedViewModelFactory(private val repository: Repository , private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            return FeedViewModel(repository , application) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
