package com.example.fridgeChefAIApp.ui.recipeFragments.recipeDetailsFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgeChefAIApp.Repository
import com.example.fridgeChefAIApp.api.model.ExtraDetailsResponse
import com.example.fridgeChefAIApp.api.model.Ingredient
import com.example.fridgeChefAIApp.api.model.Recipe
import com.example.fridgeChefAIApp.room_DB.model.FavoriteRecipe
import com.example.fridgeChefAIApp.room_DB.model.ToBuyIngredient
import com.example.fridgeChefAIApp.ui.chatBotServiceFragment.ChatBotServiceViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RecipeDetailsViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    suspend fun getRecipeIngredients(recipeId: Int): List<Ingredient>{
        return withContext(Dispatchers.IO){
            repository.getRecipeIngredients(recipeId = recipeId)
        }
    }

    suspend fun getAiRecipeIngredients(recipeId: Int): String {
        return repository.getAiRecipeIngredients(recipeId)
    }

    suspend fun getAiIngredientImage(title: String): String {
        return repository.getRecipeOrIngredientImage(title)
    }

    fun checkFavorite(recipeId: Int): Flow<Boolean> {
        return repository.isFavoriteRecipeExists(recipeId)
    }

    fun checkCooked(recipeId: Int): Flow<Boolean> {
        return repository.isCookedRecipeExists(recipeId)
    }

    fun updateFavRecipe(recipe: Recipe) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFavoriteRecipe(
                recipe.id,
                recipe.readyInMinutes,
                recipe.servings,
                recipe.summary
            )
        }
    }

    fun onAddToCartClick(ingredient: Ingredient) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertToBuyIngredient(
                ToBuyIngredient(
                    name = ingredient.name,
                    image = ingredient.image!!,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun onLoveClick(recipe: Recipe) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertFavoriteRecipe(
                FavoriteRecipe(
                    id = recipe.id,
                    title = recipe.title,
                    image = recipe.image!!,
                    readyInMinutes = recipe.readyInMinutes,
                    servings = recipe.servings,
                    createdAt = System.currentTimeMillis(),
                    summary = recipe.summary
                )
            )
            repository.deleteAiRecipe(recipe.id)
        }
    }

    fun onDislikeClick(recipe: Recipe) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFavoriteRecipe(recipe.id)
        }
    }

    suspend fun getRecipeInfo(id: Int): ExtraDetailsResponse {
        return withContext(Dispatchers.IO){
            repository.getRecipeInfo(id);
        }
    }

    suspend fun summarizeSummary(summary: String): String {
        return withContext(Dispatchers.IO){
            val chatBotServiceViewModel = ChatBotServiceViewModel(repository)
            chatBotServiceViewModel.summarizeSummary(summary)
        }
    }
}