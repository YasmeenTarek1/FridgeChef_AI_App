package com.example.recipeapp.ui.feedFragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.Recipe
import com.example.recipeapp.room_DB.model.FavoriteRecipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FeedViewModel(application: Application ,private val repository: Repository) : AndroidViewModel(application) {

    val context = getApplication<Application>().applicationContext
    val recipes: Flow<PagingData<Recipe>> = repository.getSimilarRecipesForFavorites(context).cachedIn(viewModelScope)

    fun onLoveClick(recipe: Recipe) {
        viewModelScope.launch {
            repository.insertFavoriteRecipe(FavoriteRecipe(
                id = recipe.id,
                title = recipe.title,
                image = recipe.image,
                readyInMinutes = recipe.readyInMinutes,
                servings = recipe.servings,
                likes = recipe.likes,
                healthScore = recipe.healthScore,
            ))
        }
    }
}