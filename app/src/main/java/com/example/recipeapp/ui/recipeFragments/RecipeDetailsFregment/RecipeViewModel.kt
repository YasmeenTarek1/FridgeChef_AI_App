package com.example.recipeapp.ui.recipeFragments.RecipeDetailsFregment

import androidx.lifecycle.ViewModel
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.DetailedRecipeResponse

class RecipeViewModel(private val recipeId: Int , private val repository: Repository): ViewModel() {

    suspend fun getRecipeInfo():DetailedRecipeResponse{
        return repository.getRecipeInfo(recipeId = recipeId)
    }

}