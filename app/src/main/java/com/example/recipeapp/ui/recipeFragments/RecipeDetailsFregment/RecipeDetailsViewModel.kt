package com.example.recipeapp.ui.recipeFragments.RecipeDetailsFregment

import androidx.lifecycle.ViewModel
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.DetailedRecipeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecipeDetailsViewModel(private val recipeId: Int, private val repository: Repository): ViewModel() {

    suspend fun getRecipeInfo():DetailedRecipeResponse{
        return withContext(Dispatchers.IO){
            repository.getRecipeInfo(recipeId = recipeId)
        }
    }

}