package com.example.recipeapp.ui.userFragments.userEditProfileFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.Recipe
import com.example.recipeapp.room_DB.model.FavoriteRecipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class UserEditProfileViewModel(private val repository: Repository) : ViewModel() {

    val recipes: Flow<List<FavoriteRecipe>> = repository.getAllFavoriteRecipes()

    fun onLoveClick(recipe: Recipe) {
        viewModelScope.launch {
            repository.insertFavoriteRecipe(
                FavoriteRecipe(
                    id = recipe.id,
                    title = recipe.title,
                    image = recipe.image,
                    readyInMinutes = recipe.readyInMinutes,
                    servings = recipe.servings,
                    likes = recipe.likes,
                    healthScore = recipe.healthScore,
                )
            )
        }
    }
}