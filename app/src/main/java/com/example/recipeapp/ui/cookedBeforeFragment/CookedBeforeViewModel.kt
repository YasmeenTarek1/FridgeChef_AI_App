package com.example.recipeapp.ui.cookedBeforeFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.Repository
import com.example.recipeapp.room_DB.model.CookedRecipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CookedBeforeViewModel(private val repository: Repository) : ViewModel() {

    val recipes: Flow<List<CookedRecipe>> = repository.getAllCookedRecipes()
    private val cookedRecipes: Flow<List<CookedRecipe>> = repository.getAllCookedRecipes()

    init {
        viewModelScope.launch (Dispatchers.IO) {
            repository.listenForFirestoreChangesInCookedRecipes()
        }
    }

    fun deleteRecipe(recipe: CookedRecipe){
        viewModelScope.launch (Dispatchers.IO) {
            repository.deleteCookedRecipe(recipe.id)
            val currentCookedRecipes = cookedRecipes.first()
            repository.updateCookedRecipesInFirestore(currentCookedRecipes)
        }
    }
}
