package com.example.recipeapp.ui.recipeFragments.RecipeStepsFragment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.Step
import com.example.recipeapp.room_DB.model.CookedRecipe
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class RecipeStepsViewModel(private val recipeId: Int, private val repository: Repository): ViewModel() {

    fun getSteps(): List<Step>{
        var steps:List<Step>?

        runBlocking{
             steps = repository.getSteps(recipeId = recipeId)
        }

        Log.d("RecipeViewModel", "Recipe: $steps")
        return steps!!
    }

    fun addToCookedRecipes(cookedRecipe: CookedRecipe){
        viewModelScope.launch{
            repository.insertCookedRecipe(cookedRecipe)
        }
    }

}