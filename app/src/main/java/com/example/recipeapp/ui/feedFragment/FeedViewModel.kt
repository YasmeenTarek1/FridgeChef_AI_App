package com.example.recipeapp.ui.feedFragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.Recipe
import com.example.recipeapp.room_DB.model.FavoriteRecipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FeedViewModel(application: Application ,private val repository: Repository) : AndroidViewModel(application) {

    val recipes: Flow<PagingData<Recipe>> = repository.getSimilarRecipesForFavorites(repository).cachedIn(viewModelScope)

    fun onLoveClick(recipe: Recipe) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertFavoriteRecipe(FavoriteRecipe(
                id = recipe.id,
                title = recipe.title,
                image = recipe.image,
                readyInMinutes = recipe.readyInMinutes,
                servings = recipe.servings,
                likes = recipe.likes,
                healthScore = recipe.healthScore,
                createdAt = System.currentTimeMillis()
            ))
        }
    }

    suspend fun checkFavorite(recipeId: Int): Boolean {
        return repository.isFavoriteRecipeExists(recipeId)
    }

    suspend fun clearAllToFavoriteRecipes() {
        return repository.clearAllToFavoriteRecipes()
    }

    suspend fun clearAllCookedRecipes() {
        return repository.clearAllCookedRecipes()
    }

    suspend fun clearAllToBuyIngredients() {
        return repository.clearAllToBuyIngredients()
    }

    suspend fun clearAllAiRecipes() {
        return repository.clearAllAiRecipes()
    }
}