package com.example.fridgeChefAIApp.ui.specialRecipesFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgeChefAIApp.Repository
import com.example.fridgeChefAIApp.room_DB.model.AiRecipe
import com.example.fridgeChefAIApp.room_DB.model.CookedRecipe
import com.example.fridgeChefAIApp.room_DB.model.FavoriteRecipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpecialRecipesViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

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