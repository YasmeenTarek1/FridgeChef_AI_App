package com.example.recipeapp.ui.chatBotServiceFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recipeapp.Repository
import com.example.recipeapp.sharedPreferences.SharedPreferences

class ChatBotServiceViewModelFactory(private val repository: Repository, private val sharedPreferences: SharedPreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatBotServiceViewModel::class.java)) {
            return ChatBotServiceViewModel(repository , sharedPreferences) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
