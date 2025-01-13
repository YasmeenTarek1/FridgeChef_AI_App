package com.example.fridgeChefAIApp.ui.userFragments.userInfoFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgeChefAIApp.Repository
import com.example.fridgeChefAIApp.room_DB.model.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    fun insertUser(userInfo:UserInfo){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertUser(userInfo)
        }
    }

    suspend fun getUserById(userID: String): UserInfo?{
        return withContext(Dispatchers.IO){
            repository.getUserById(userID)
        }
    }
}