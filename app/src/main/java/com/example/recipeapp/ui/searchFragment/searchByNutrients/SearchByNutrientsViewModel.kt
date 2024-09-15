package com.example.recipeapp.ui.searchFragment.searchByNutrients

import androidx.lifecycle.ViewModel
import com.example.recipeapp.api.model.Recipe
import com.example.recipeapp.Repository
import kotlinx.coroutines.runBlocking

class SearchByNutrientsViewModel(private val repository: Repository): ViewModel() {

    fun searchRecipesByNutrients(
        minCarbs: Int? = null, maxCarbs: Int? = null,minProtein: Int? = null,maxProtein: Int? = null,minFat: Int? = null,maxFat: Int? = null,
        minCalories: Int? = null,maxCalories: Int? = null,minSugar: Int? = null, maxSugar: Int? = null)
    :List<Recipe> {
        var recipe:List<Recipe>?

        runBlocking{
             recipe = repository.searchRecipesByNutrients(minCarbs, maxCarbs,minProtein,maxProtein ,minFat,maxFat, minCalories,maxCalories,minSugar, maxSugar)
        }
        return recipe!!
    }

}