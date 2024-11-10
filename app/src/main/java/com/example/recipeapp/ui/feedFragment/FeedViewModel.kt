package com.example.recipeapp.ui.feedFragment

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.Recipe
import com.example.recipeapp.room_DB.model.FavoriteRecipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FeedViewModel(private val repository: Repository, application: Application) : AndroidViewModel(application) {

    // LiveData to track retry attempts
    private val retryTrigger = MutableLiveData<Unit>()

    // Paging data with retry handling
    val recipes: Flow<PagingData<Recipe>> = repository.getSimilarRecipesForFavorites(repository, application.baseContext)
        .cachedIn(viewModelScope)
        .onEach { retryTrigger.value = Unit }
        .catch { throwable ->
            Log.d("Error" , "Error in Paging")
            throwable.printStackTrace()
        }

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
            favRecipes.collect { favRecipes ->
                repository.updateFavRecipesInFirestore(favRecipes)
            }
        }
    }

    fun onDislikeClick(recipe: Recipe) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFavoriteRecipe(recipe.id)
            favRecipes.collect { favRecipes ->
                repository.updateFavRecipesInFirestore(favRecipes)
            }
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

    fun retry() {
        retryTrigger.value = Unit
    }
}
