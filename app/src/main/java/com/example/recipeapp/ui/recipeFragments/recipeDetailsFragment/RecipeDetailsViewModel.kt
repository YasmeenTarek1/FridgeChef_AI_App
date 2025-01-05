package com.example.recipeapp.ui.recipeFragments.recipeDetailsFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.ExtraDetailsResponse
import com.example.recipeapp.api.model.Ingredient
import com.example.recipeapp.api.model.Recipe
import com.example.recipeapp.room_DB.model.AiRecipe
import com.example.recipeapp.room_DB.model.FavoriteRecipe
import com.example.recipeapp.room_DB.model.ToBuyIngredient
import com.example.recipeapp.ui.chatBotServiceFragment.ChatBotServiceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeDetailsViewModel(private val repository: Repository): ViewModel() {

    private val toBuyIngredients: Flow<List<ToBuyIngredient>> = repository.getAllToBuyIngredients()
    private val favRecipes: Flow<List<FavoriteRecipe>> = repository.getAllFavoriteRecipes()
    private val aiRecipes: Flow<List<AiRecipe>> = repository.getAllAiRecipes()


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

    fun onAddToCartClick(ingredient: Ingredient) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertToBuyIngredient(
                ToBuyIngredient(
                    name = ingredient.name,
                    image = ingredient.image!!,
                    createdAt = System.currentTimeMillis()
                )
            )

            toBuyIngredients.collect { toBuyIngredients ->
                repository.updateToBuyIngredientsInFirestore(toBuyIngredients)
            }
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
            favRecipes.collect { favRecipes ->
                repository.updateFavRecipesInFirestore(favRecipes)
            }

            repository.deleteAiRecipe(recipe.id)
            aiRecipes.collect { recipes ->
                repository.updateAiRecipesInFirestore(recipes)
            }
        }
    }

    fun onDislikeClick(recipe: Recipe) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFavoriteRecipe(recipe.id)
            val currentFavRecipes = favRecipes.first()
            repository.updateFavRecipesInFirestore(currentFavRecipes)
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