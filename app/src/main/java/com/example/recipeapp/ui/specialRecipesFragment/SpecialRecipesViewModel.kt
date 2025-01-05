package com.example.recipeapp.ui.specialRecipesFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.Repository
import com.example.recipeapp.room_DB.model.AiRecipe
import com.example.recipeapp.room_DB.model.CookedRecipe
import com.example.recipeapp.room_DB.model.FavoriteRecipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SpecialRecipesViewModel(repository: Repository): ViewModel() {

    val cookedRecipes: Flow<List<CookedRecipe>> = repository.getAllCookedRecipes()
    val favRecipes: Flow<List<FavoriteRecipe>> = repository.getAllFavoriteRecipes()
    val aiRecipes: Flow<List<AiRecipe>> = repository.getAllAiRecipes()

    init{
        viewModelScope.launch (Dispatchers.IO) {
            repository.listenForFirestoreChangesInFavoriteRecipes()
            repository.listenForFirestoreChangesInCookedRecipes()
            repository.listenForFirestoreChangesInAiRecipes()
        }
    }
}