package com.example.recipeapp.ui.searchFragment.searchByNutrients

import androidx.lifecycle.ViewModel
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchByNutrientsViewModel(private val repository: Repository): ViewModel() {

    suspend fun performSearch(
        maxCarbs: Int? = null, maxProtein: Int? = null, maxFat: Int? = null, maxCalories: Int? = null, maxSugar: Int? = null)
    :List<Recipe> {
        return withContext(Dispatchers.IO) {
            repository.searchRecipesByNutrients(
                maxCarbs,
                maxProtein,
                maxFat,
                maxCalories,
                maxSugar
            )
        }
    }

}
