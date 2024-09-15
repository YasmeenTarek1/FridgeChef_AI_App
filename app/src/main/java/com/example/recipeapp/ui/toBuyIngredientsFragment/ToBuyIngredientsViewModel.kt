package com.example.recipeapp.ui.toBuyIngredientsFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.AppUser
import com.example.recipeapp.Repository
import com.example.recipeapp.room_DB.model.ToBuyIngredient
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ToBuyIngredientsViewModel(private val repository: Repository) : ViewModel() {

    val ingredients: Flow<List<ToBuyIngredient>> = repository.getAllToBuyIngredients()

    init {
        observeRoomChangesAndSyncWithFirestore()
        repository.listenForFirestoreChangesInToBuyIngredients()
    }

    private fun observeRoomChangesAndSyncWithFirestore() {
        viewModelScope.launch {
            ingredients.collect { ingredients ->
                syncWithFirestore(ingredients)
            }
        }
    }

    private fun syncWithFirestore(roomIngredients: List<ToBuyIngredient>) {
        val firestore = FirebaseFirestore.getInstance()
        val userId = AppUser.instance?.userId!!
        val toBuyIngredientsCollection = firestore.collection("users").document(userId).collection("To-Buy Ingredients")

        toBuyIngredientsCollection.get().addOnSuccessListener { snapshot ->
            val firestoreRecipes = snapshot.documents.map { document ->
                document.toObject(ToBuyIngredient::class.java)!!
            }

            val recipesToAddOrUpdate = roomIngredients.filter { ingredient ->
                !firestoreRecipes.contains(ingredient)  // Recipes that are new or updated
            }

            val recipesToDelete = firestoreRecipes.filter { ingredient ->
                !roomIngredients.contains(ingredient)  // Recipes that are removed in Room
            }

            // 1. Add or update recipes
            recipesToAddOrUpdate.forEach { ingredient ->
                toBuyIngredientsCollection.document(ingredient.id.toString()).set(ingredient)
            }

            // 2. Delete removed recipes
            recipesToDelete.forEach { ingredient ->
                toBuyIngredientsCollection.document(ingredient.id.toString()).delete()
            }
        }
    }
}
