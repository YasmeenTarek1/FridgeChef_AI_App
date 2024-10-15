package com.example.recipeapp.ui.searchFragment.searchByIngredientsFragment

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.recipeapp.AppUser
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.Ingredient
import com.example.recipeapp.api.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class SearchByIngredientsViewModel(private val repository: Repository): ViewModel() {

    suspend fun searchRecipesByIngredients(ingredients: List<String>):List<Recipe>{
        return withContext(Dispatchers.IO){
             repository.searchRecipesByIngredients(ingredients)
        }
    }

    fun autocompleteIngredientSearch(query: String): Flow<List<Ingredient>> = flow {
        try {
            val result = repository.autocompleteIngredientSearch(query)
            emit(result)
        }catch (e: Exception) {
            Log.d("Error" , "Error in auto complete ingredients")
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)


    suspend fun getUserName():String{
        return withContext(Dispatchers.IO){
            repository.getUserById(AppUser.instance!!.userId!!)!!.name.substringBefore(" ")
        }
    }

    suspend fun getUserImage():String{
        return withContext(Dispatchers.IO){
            repository.getUserById(AppUser.instance!!.userId!!)!!.image!!
        }
    }
}