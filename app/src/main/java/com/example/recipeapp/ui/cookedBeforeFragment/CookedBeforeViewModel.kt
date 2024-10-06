package com.example.recipeapp.ui.cookedBeforeFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.AppUser
import com.example.recipeapp.Repository
import com.example.recipeapp.room_DB.model.CookedRecipe
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CookedBeforeViewModel(private val repository: Repository) : ViewModel() {

    val recipes: Flow<List<CookedRecipe>> = repository.getAllCookedRecipes()

    init {
        observeRoomChangesAndSyncWithFirestore()
        repository.listenForFirestoreChangesInCookedRecipes()
    }

    private fun observeRoomChangesAndSyncWithFirestore() {
        viewModelScope.launch {
            recipes.collect { recipes ->
                syncWithFirestore(recipes)
            }
        }
    }

    private fun syncWithFirestore(roomRecipes: List<CookedRecipe>) {
        val firestore = FirebaseFirestore.getInstance()
        val userId = AppUser.instance?.userId!!
        val cookedRecipesCollection =  firestore.collection("users").document(userId).collection("Cooked Recipes")

        cookedRecipesCollection.get().addOnSuccessListener { snapshot ->
            val firestoreRecipes = snapshot.documents.map { document ->
                document.toObject(CookedRecipe::class.java)!!
            }

            val recipesToAddOrUpdate = roomRecipes.filter { recipe ->
                !firestoreRecipes.contains(recipe)  // Recipes that are new or updated
            }

            val recipesToDelete = firestoreRecipes.filter { recipe ->
                !roomRecipes.contains(recipe)  // Recipes that are removed from Room
            }

            // 1. Add or update recipes
            recipesToAddOrUpdate.forEach { recipe ->
                cookedRecipesCollection.document(recipe.id.toString()).set(recipe)
            }

            // 2. Delete removed recipes
            recipesToDelete.forEach { recipe ->
                cookedRecipesCollection.document(recipe.id.toString()).delete()
            }
        }
    }
}
