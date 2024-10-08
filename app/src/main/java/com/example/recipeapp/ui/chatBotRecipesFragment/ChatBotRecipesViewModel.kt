package com.example.recipeapp.ui.chatBotRecipesFragment

import androidx.lifecycle.ViewModel
import com.example.recipeapp.Repository
import com.example.recipeapp.room_DB.model.AiRecipe
import kotlinx.coroutines.flow.Flow

class ChatBotRecipesViewModel(private val repository: Repository) : ViewModel() {

    val recipes: Flow<List<AiRecipe>> = repository.getAllAiRecipes()

    init {
        repository.listenForFirestoreChangesInAiRecipes()
    }
}
