package com.example.recipeapp.ui.toBuyIngredientsFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.Repository
import com.example.recipeapp.room_DB.model.ToBuyIngredient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ToBuyIngredientsViewModel(private val repository: Repository) : ViewModel() {

    val ingredients: Flow<List<ToBuyIngredient>> = repository.getAllToBuyIngredients()

    init {
        repository.listenForFirestoreChangesInToBuyIngredients()
    }

    fun deleteIngredient(ingredient: ToBuyIngredient){
        viewModelScope.launch (Dispatchers.IO) {
            repository.deleteIngredient(ingredient)

            ingredients.collect { ingredients ->
                repository.updateToBuyIngredientsInFirestore(ingredients)
            }
        }
    }

}
