package com.example.fridgeChefAIApp.ui.recipeFragments.recipeStepsFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgeChefAIApp.AppUser
import com.example.fridgeChefAIApp.Repository
import com.example.fridgeChefAIApp.api.model.Step
import com.example.fridgeChefAIApp.room_DB.model.CookedRecipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RecipeStepsViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

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
            repository.deleteFavoriteRecipe(cookedRecipe.id)
            repository.deleteAiRecipe(cookedRecipe.id)
        }
    }

    suspend fun getUserName(): String{
        return withContext(Dispatchers.IO){
            repository.getUserById(AppUser.instance!!.userId!!)!!.name.substringBefore(" ")
        }
    }

}