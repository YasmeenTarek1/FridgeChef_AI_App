package com.example.recipeapp.ui.chatBotRecipesFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.Repository
import com.example.recipeapp.room_DB.model.AiRecipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ChatBotRecipesViewModel(private val repository: Repository) : ViewModel() {

    val recipes: Flow<List<AiRecipe>> = repository.getAllAiRecipes()

    init {
        repository.listenForFirestoreChangesInAiRecipes()
    }

    fun deleteRecipe(recipe: AiRecipe){
        viewModelScope.launch (Dispatchers.IO) {
            repository.deleteAiRecipe(recipe)
            recipes.collect { recipes ->
                repository.updateAiRecipesInFirestore(recipes)
            }
        }
    }
}
