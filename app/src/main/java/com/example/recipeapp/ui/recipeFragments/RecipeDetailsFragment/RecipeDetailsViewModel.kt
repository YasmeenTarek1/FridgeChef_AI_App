package com.example.recipeapp.ui.recipeFragments.RecipeDetailsFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.Ingredient
import com.example.recipeapp.room_DB.model.ToBuyIngredient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeDetailsViewModel(private val repository: Repository): ViewModel() {

    private val toBuyIngredients: Flow<List<ToBuyIngredient>> = repository.getAllToBuyIngredients()

    suspend fun getRecipeIngredients(recipeId: Int): List<Ingredient>{
        return withContext(Dispatchers.IO){
            repository.getRecipeIngredients(recipeId = recipeId)
        }
    }

    fun onAddToCartClick(ingredient: Ingredient) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertToBuyIngredient(
                ToBuyIngredient(
                    name = ingredient.name,
                    image = ingredient.image!!,
                    createdAt = System.currentTimeMillis()
                )
            )

            toBuyIngredients.collect { toBuyIngredients ->
                repository.updateToBuyIngredientsInFirestore(toBuyIngredients)
            }
        }
    }

}