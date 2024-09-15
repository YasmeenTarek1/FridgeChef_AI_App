package com.example.recipeapp.ui.recipeFragments.RecipeDetailsFregment

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.recipeapp.api.model.DetailedRecipeResponse
import com.example.recipeapp.Repository
import kotlinx.coroutines.runBlocking

class RecipeViewModel(private val recipeId: Int , private val repository: Repository): ViewModel() {

    fun getRecipeInfo():DetailedRecipeResponse{
        var recipe:DetailedRecipeResponse? = null
        runBlocking{
             recipe = repository.getRecipeInfo(recipeId = recipeId)
        }
        Log.d("RecipeViewModel", "Recipe: $recipeId")
        Log.d("RecipeViewModel", "Recipe: $recipe")
        return recipe!!
    }

}