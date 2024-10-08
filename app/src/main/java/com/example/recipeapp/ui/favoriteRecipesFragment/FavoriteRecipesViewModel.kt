package com.example.recipeapp.ui.favoriteRecipesFragment

import androidx.lifecycle.ViewModel
import com.example.recipeapp.Repository
import com.example.recipeapp.room_DB.model.FavoriteRecipe
import kotlinx.coroutines.flow.Flow

class FavoriteRecipesViewModel(private val repository: Repository) : ViewModel() {

    val recipes: Flow<List<FavoriteRecipe>> = repository.getAllFavoriteRecipes()

    init {
        repository.listenForFirestoreChangesInFavoriteRecipes()
    }

}