package com.example.recipeapp.ui.favoriteRecipesFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.Repository
import com.example.recipeapp.room_DB.model.FavoriteRecipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FavoriteRecipesViewModel(private val repository: Repository) : ViewModel() {

    val recipes: Flow<List<FavoriteRecipe>> = repository.getAllFavoriteRecipes()

    init {
        repository.listenForFirestoreChangesInFavoriteRecipes()
    }

    fun deleteRecipe(recipe: FavoriteRecipe){
        viewModelScope.launch (Dispatchers.IO) {

            repository.deleteFavoriteRecipe(recipe.id)

            recipes.collect { recipes ->
                repository.updateFavRecipesInFirestore(recipes)
            }

        }
    }

}