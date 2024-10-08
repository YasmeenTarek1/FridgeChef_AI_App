package com.example.recipeapp.ui.cookedBeforeFragment

import androidx.lifecycle.ViewModel
import com.example.recipeapp.Repository
import com.example.recipeapp.room_DB.model.CookedRecipe
import kotlinx.coroutines.flow.Flow

class CookedBeforeViewModel(private val repository: Repository) : ViewModel() {

    val recipes: Flow<List<CookedRecipe>> = repository.getAllCookedRecipes()

    init {
        repository.listenForFirestoreChangesInCookedRecipes()
    }

}
