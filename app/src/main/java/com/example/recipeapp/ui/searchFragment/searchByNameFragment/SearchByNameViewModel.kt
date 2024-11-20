package com.example.recipeapp.ui.searchFragment.searchByNameFragment

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.recipeapp.AppUser
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class SearchByNameViewModel(private val repository: Repository): ViewModel() {

    suspend fun searchRecipesByName(recipeName: String):List<Recipe>{
        return withContext(Dispatchers.IO){
            repository.searchRecipesByName(recipeName)
        }
    }

    fun autocompleteRecipeSearch(query: String): Flow<List<Recipe>> = flow {
        try {
            val result = repository.autocompleteRecipeSearch(query)
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

    suspend fun getUserImage():String?{
        return repository.getUserById(AppUser.instance!!.userId!!)!!.image
    }
}