package com.example.recipeapp.ui.userFragments.userInfoFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.Repository
import com.example.recipeapp.room_DB.model.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserInfoViewModel (private val repository: Repository): ViewModel() {

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