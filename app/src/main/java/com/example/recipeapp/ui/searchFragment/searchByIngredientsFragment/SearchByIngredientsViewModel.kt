package com.example.recipeapp.ui.searchFragment.searchByIngredientsFragment

import androidx.lifecycle.ViewModel
import com.example.recipeapp.api.model.Recipe
import com.example.recipeapp.Repository
import kotlinx.coroutines.runBlocking

class SearchByIngredientsViewModel(private val repository: Repository): ViewModel() {


    fun searchRecipesByIngredients(ingredients: List<String>):List<Recipe>{
        var recipe:List<Recipe>?
        runBlocking{
             recipe = repository.searchRecipesByIngredients(ingredients)
        }
        return recipe!!
    }

}