package com.example.recipeapp.ui.feedFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.Recipe
import com.example.recipeapp.room_DB.model.FavoriteRecipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FeedViewModel(private val repository: Repository) : ViewModel() {

    val recipes: Flow<PagingData<Recipe>> = repository.getSimilarRecipesForFavorites(repository).cachedIn(viewModelScope)
    val favRecipes: Flow<List<FavoriteRecipe>> = repository.getAllFavoriteRecipes()

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
            favRecipes.collect { favRecipes ->
                repository.updateFavRecipesInFirestore(favRecipes)
            }
        }
    }

    suspend fun checkFavorite(recipeId: Int): Boolean {
        return repository.isFavoriteRecipeExists(recipeId)
    }

    suspend fun clearAllInfo() {
        repository.clearAllToFavoriteRecipes()
        repository.clearAllCookedRecipes()
        repository.clearAllToBuyIngredients()
        repository.clearAllAiRecipes()
    }

}