package com.example.recipeapp.ui.searchFragment.searchByNameFragment

import androidx.lifecycle.ViewModel
import com.example.recipeapp.api.model.Recipe
import com.example.recipeapp.Repository
import kotlinx.coroutines.runBlocking

class SearchByNameViewModel(private val repository: Repository): ViewModel() {

    fun searchRecipesByName(recipeName: String):List<Recipe>{
        var recipe:List<Recipe>?
        runBlocking{
            recipe = repository.searchRecipesByName(recipeName)
        }
        return recipe!!
    }
}