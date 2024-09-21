package com.example.recipeapp.ui.searchFragment.searchByIngredientsFragment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.Ingredient
import com.example.recipeapp.api.model.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class SearchByIngredientsViewModel(private val repository: Repository): ViewModel() {


    fun searchRecipesByIngredients(ingredients: List<String>):List<Recipe>{
        var recipe:List<Recipe> = emptyList()
        viewModelScope.launch{
             recipe = repository.searchRecipesByIngredients(ingredients)
        }
        return recipe
    }

    fun autocompleteIngredientSearch(query: String): Flow<List<Ingredient>> = flow {
        try {
            val result = repository.autocompleteIngredientSearch(query)
            emit(result)
        }catch (e: Exception) {
            Log.d("Error" , "Error in auto complete ingredients")
            emit(emptyList())
        }
    }


}