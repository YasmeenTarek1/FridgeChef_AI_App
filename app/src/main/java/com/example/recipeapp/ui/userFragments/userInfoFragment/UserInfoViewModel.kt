package com.example.recipeapp.ui.userFragments.userInfoFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.Repository
import com.example.recipeapp.room_DB.model.UserInfo
import kotlinx.coroutines.launch

class UserInfoViewModel (private val repository: Repository): ViewModel() {

    fun insertUser(userInfo:UserInfo){
        viewModelScope.launch {
            repository.insertUser(userInfo)
        }
    }

}