package com.example.recipeapp.ui.cookedBeforeFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.Repository
import com.example.recipeapp.room_DB.model.CookedRecipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CookedBeforeViewModel(private val repository: Repository) : ViewModel() {

    val recipes: Flow<List<CookedRecipe>> = repository.getAllCookedRecipes()

    init {
        repository.listenForFirestoreChangesInCookedRecipes()
    }

    fun deleteRecipe(recipe: CookedRecipe){
        viewModelScope.launch (Dispatchers.IO) {
            repository.deleteCookedRecipe(recipe.id)

            recipes.collect { recipes ->
                repository.updateCookedRecipesInFirestore(recipes)

            }

        }
    }
}
