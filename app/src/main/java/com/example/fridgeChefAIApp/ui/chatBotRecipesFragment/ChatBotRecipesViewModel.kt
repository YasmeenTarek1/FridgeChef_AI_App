package com.example.fridgeChefAIApp.ui.chatBotRecipesFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgeChefAIApp.Repository
import com.example.fridgeChefAIApp.room_DB.model.AiRecipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatBotRecipesViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    val recipes: Flow<List<AiRecipe>> = repository.getAllAiRecipes()

    init {
        viewModelScope.launch (Dispatchers.IO){
            repository.listenForFirestoreChangesInAiRecipes()
        }
    }

    fun deleteRecipe(recipe: AiRecipe){
        viewModelScope.launch (Dispatchers.IO) {
            repository.deleteAiRecipe(recipe.id)
        }
    }
}
