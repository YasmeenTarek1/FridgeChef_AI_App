package com.example.recipeapp.ui.recipeFragments.RecipeDetailsFragment

import androidx.lifecycle.ViewModel
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.ExtraDetailsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecipeDetailsViewModel(private val repository: Repository): ViewModel() {

    suspend fun getRecipeInfo(recipeId: Int):ExtraDetailsResponse{
        return withContext(Dispatchers.IO){
            repository.getRecipeInfo(recipeId = recipeId)
        }
    }

}