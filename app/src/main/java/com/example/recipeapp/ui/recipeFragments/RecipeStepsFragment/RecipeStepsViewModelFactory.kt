package com.example.recipeapp.ui.recipeFragments.RecipeStepsFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recipeapp.Repository

class RecipeStepsViewModelFactory(private val id: Int, private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeStepsViewModel::class.java)) {
            return RecipeStepsViewModel(id , repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
