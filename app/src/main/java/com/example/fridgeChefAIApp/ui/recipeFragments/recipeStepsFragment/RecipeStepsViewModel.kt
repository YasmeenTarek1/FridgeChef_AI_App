package com.example.fridgeChefAIApp.ui.recipeFragments.recipeStepsFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgeChefAIApp.AppUser
import com.example.fridgeChefAIApp.Repository
import com.example.fridgeChefAIApp.api.model.Step
import com.example.fridgeChefAIApp.room_DB.model.AiRecipe
import com.example.fridgeChefAIApp.room_DB.model.CookedRecipe
import com.example.fridgeChefAIApp.room_DB.model.FavoriteRecipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RecipeStepsViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val cookedRecipes: Flow<List<CookedRecipe>> = repository.getAllCookedRecipes()
    private val favRecipes: Flow<List<FavoriteRecipe>> = repository.getAllFavoriteRecipes()
    private val aiRecipes: Flow<List<AiRecipe>> = repository.getAllAiRecipes()

    suspend fun getSteps(recipeId: Int): List<Step>{
        return withContext(Dispatchers.IO) {
            repository.getSteps(recipeId = recipeId)
        }
    }

    suspend fun getAiRecipeSteps(recipeId: Int): String {
        return repository.getAiRecipeSteps(recipeId)
    }

    fun addToCookedRecipes(cookedRecipe: CookedRecipe){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertCookedRecipe(cookedRecipe)
            val currentCookedRecipes = cookedRecipes.first()
            repository.updateCookedRecipesInFirestore(currentCookedRecipes)

            repository.deleteFavoriteRecipe(cookedRecipe.id)
            val currentFavRecipes = favRecipes.first()
            repository.updateFavRecipesInFirestore(currentFavRecipes)

            repository.deleteAiRecipe(cookedRecipe.id)
            val currentAiRecipes = aiRecipes.first()
            repository.updateAiRecipesInFirestore(currentAiRecipes)
        }
    }

    suspend fun getUserName(): String{
        return withContext(Dispatchers.IO){
            repository.getUserById(AppUser.instance!!.userId!!)!!.name.substringBefore(" ")
        }
    }

}