package com.example.fridgeChefAIApp.ui.userFragments.userProfileFragment

import androidx.lifecycle.ViewModel
import com.example.fridgeChefAIApp.Repository
import com.example.fridgeChefAIApp.room_DB.model.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    suspend fun getUserById(userID: String): UserInfo? {
        return withContext(Dispatchers.IO) {
            repository.getUserById(userID)
        }
    }

}