package com.example.recipeapp.ui.searchFragment.searchByNameFragment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class SearchByNameViewModel(private val repository: Repository): ViewModel() {

    fun searchRecipesByName(recipeName: String):List<Recipe>{
        var recipe:List<Recipe> = emptyList()
        viewModelScope.launch{
            recipe = repository.searchRecipesByName(recipeName)
        }
        return recipe
    }

    fun autocompleteRecipeSearch(query: String): Flow<List<Recipe>> = flow {
        try {
            val result = repository.autocompleteRecipeSearch(query)
            emit(result)
        }catch (e: Exception) {
            Log.d("Error" , "Error in auto complete ingredients")
            emit(emptyList())
        }
    }
}