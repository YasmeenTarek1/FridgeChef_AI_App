package com.example.fridgeChefAIApp.ui.feedFragment


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.fridgeChefAIApp.Repository
import com.example.fridgeChefAIApp.api.model.Recipe
import com.example.fridgeChefAIApp.room_DB.model.FavoriteRecipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    val recipes: Flow<PagingData<Recipe>> = repository.getSimilarRecipesForFavorites().cachedIn(viewModelScope)

    private val favRecipes: Flow<List<FavoriteRecipe>> = repository.getAllFavoriteRecipes()

    fun onLoveClick(recipe: Recipe) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertFavoriteRecipe(
                FavoriteRecipe(
                    id = recipe.id,
                    title = recipe.title,
                    image = recipe.image!!,
                    readyInMinutes = recipe.readyInMinutes,
                    servings = recipe.servings,
                    createdAt = System.currentTimeMillis(),
                    summary = recipe.summary
                )
            )
            val currentFavRecipes = favRecipes.first()
            repository.updateFavRecipesInFirestore(currentFavRecipes)
        }
    }

    fun onDislikeClick(recipe: Recipe) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFavoriteRecipe(recipe.id)
            val currentFavRecipes = favRecipes.first()
            repository.updateFavRecipesInFirestore(currentFavRecipes)
        }
    }

    fun checkFavorite(recipeId: Int): Flow<Boolean> {
        return repository.isFavoriteRecipeExists(recipeId)
    }

    suspend fun clearAllInfo() {
        repository.clearAllToFavoriteRecipes()
        repository.clearAllCookedRecipes()
        repository.clearAllToBuyIngredients()
        repository.clearAllAiRecipes()
    }
}
