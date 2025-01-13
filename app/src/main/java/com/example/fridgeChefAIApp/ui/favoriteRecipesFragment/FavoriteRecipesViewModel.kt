package com.example.fridgeChefAIApp.ui.favoriteRecipesFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgeChefAIApp.Repository
import com.example.fridgeChefAIApp.room_DB.model.FavoriteRecipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteRecipesViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    val recipes: Flow<List<FavoriteRecipe>> = repository.getAllFavoriteRecipes()
    private val favRecipes: Flow<List<FavoriteRecipe>> = repository.getAllFavoriteRecipes()

    init {
        viewModelScope.launch (Dispatchers.IO) {
            repository.listenForFirestoreChangesInFavoriteRecipes()
        }
    }

    fun deleteRecipe(recipe: FavoriteRecipe){
        viewModelScope.launch (Dispatchers.IO) {
            repository.deleteFavoriteRecipe(recipe.id)
            val currentFavRecipes = favRecipes.first()
            repository.updateFavRecipesInFirestore(currentFavRecipes)
        }
    }

}