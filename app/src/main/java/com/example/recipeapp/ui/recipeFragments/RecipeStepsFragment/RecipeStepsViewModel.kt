package com.example.recipeapp.ui.recipeFragments.RecipeStepsFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.AppUser
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.Step
import com.example.recipeapp.room_DB.model.AiRecipe
import com.example.recipeapp.room_DB.model.CookedRecipe
import com.example.recipeapp.room_DB.model.FavoriteRecipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeStepsViewModel(private val recipeId: Int, private val repository: Repository): ViewModel() {

    private val cookedRecipes: Flow<List<CookedRecipe>> = repository.getAllCookedRecipes()
    private val favRecipes: Flow<List<FavoriteRecipe>> = repository.getAllFavoriteRecipes()
    private val aiRecipes: Flow<List<AiRecipe>> = repository.getAllAiRecipes()

    suspend fun getSteps(): List<Step>{
        return withContext(Dispatchers.IO) {
            repository.getSteps(recipeId = recipeId)
        }
    }

    suspend fun getAiRecipeSteps(recipeId: Int): String {
        return repository.getAiRecipeSteps(recipeId)
    }

    fun addToCookedRecipes(cookedRecipe: CookedRecipe){
        viewModelScope.launch(Dispatchers.IO){
            repository.insertCookedRecipe(cookedRecipe)
            cookedRecipes.collect { recipes ->
                repository.updateCookedRecipesInFirestore(recipes)
            }

            repository.deleteFavoriteRecipe(cookedRecipe.id)
            favRecipes.collect { recipes ->
                repository.updateFavRecipesInFirestore(recipes)
            }
        }
    }

    fun removeFromFavRecipes(recipeID: Int){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteFavoriteRecipe(recipeID)
            favRecipes.collect { recipes ->
                repository.updateFavRecipesInFirestore(recipes)
            }
        }
    }

    fun removeFromAiRecipes(recipeID: Int){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteAiRecipe(recipeID)
            aiRecipes.collect { recipes ->
                repository.updateAiRecipesInFirestore(recipes)
            }
        }
    }

    suspend fun getUserName(): String{
        return withContext(Dispatchers.IO){
            repository.getUserById(AppUser.instance!!.userId!!)!!.name.substringBefore(" ")
        }
    }

}