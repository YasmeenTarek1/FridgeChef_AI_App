package com.example.fridgeChefAIApp.ui.toBuyIngredientsFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgeChefAIApp.Repository
import com.example.fridgeChefAIApp.room_DB.model.ToBuyIngredient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ToBuyIngredientsViewModel(private val repository: Repository) : ViewModel() {

    val ingredients: Flow<List<ToBuyIngredient>> = repository.getAllToBuyIngredients()

    init {
        viewModelScope.launch (Dispatchers.IO) {
            repository.listenForFirestoreChangesInToBuyIngredients()
        }
    }

    fun deleteIngredient(ingredient: ToBuyIngredient){
        viewModelScope.launch (Dispatchers.IO) {
            repository.deleteIngredient(ingredient)
        }
    }

}
