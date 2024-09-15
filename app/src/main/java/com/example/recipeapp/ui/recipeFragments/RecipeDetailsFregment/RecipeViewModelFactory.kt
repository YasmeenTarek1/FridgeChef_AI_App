package com.example.recipeapp.ui.recipeFragments.RecipeDetailsFregment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recipeapp.Repository

class RecipeViewModelFactory(private val id: Int , private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
            return RecipeViewModel(id , repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
