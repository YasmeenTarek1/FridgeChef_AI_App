package com.example.recipeapp.ui.recipeFragments.RecipeDetailsFragment

import androidx.lifecycle.ViewModel
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.Ingredient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecipeDetailsViewModel(private val repository: Repository): ViewModel() {

    suspend fun getRecipeIngredients(recipeId: Int): List<Ingredient>{
        return withContext(Dispatchers.IO){
            repository.getRecipeIngredients(recipeId = recipeId)
        }
    }

}