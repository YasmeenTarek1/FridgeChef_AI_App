package com.example.fridgeChefAIApp.ui.cookedBeforeFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgeChefAIApp.Repository
import com.example.fridgeChefAIApp.room_DB.model.CookedRecipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CookedBeforeViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

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
        }
    }
}
