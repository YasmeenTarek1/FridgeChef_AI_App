package com.example.fridgeChefAIApp.ui.searchFragment.searchByNutrients

import androidx.lifecycle.ViewModel
import com.example.fridgeChefAIApp.Repository
import com.example.fridgeChefAIApp.api.model.Recipe
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
