package com.example.fridgeChefAIApp.ui.chatBotServiceFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fridgeChefAIApp.Repository
import com.example.fridgeChefAIApp.sharedPreferences.SharedPreferences

class ChatBotServiceViewModelFactory(private val repository: Repository, private val sharedPreferences: SharedPreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatBotServiceViewModel::class.java)) {
            return ChatBotServiceViewModel(repository , sharedPreferences) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
