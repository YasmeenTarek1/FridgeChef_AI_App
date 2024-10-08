package com.example.recipeapp.ui.userFragments.userProfileFragment

import androidx.lifecycle.ViewModel
import com.example.recipeapp.Repository
import com.example.recipeapp.room_DB.model.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserProfileViewModel (private val repository: Repository): ViewModel() {

    suspend fun getUserById(userID: String): UserInfo? {
        return withContext(Dispatchers.IO) {
            repository.getUserById(userID)
        }
    }

}