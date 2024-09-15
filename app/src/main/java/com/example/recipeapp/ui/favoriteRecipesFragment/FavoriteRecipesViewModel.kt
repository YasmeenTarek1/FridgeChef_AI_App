package com.example.recipeapp.ui.favoriteRecipesFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.AppUser
import com.example.recipeapp.Repository
import com.example.recipeapp.room_DB.model.FavoriteRecipe
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FavoriteRecipesViewModel(private val repository: Repository) : ViewModel() {

    val recipes: Flow<List<FavoriteRecipe>> = repository.getAllFavoriteRecipes()


    init {
        observeRoomChangesAndSyncWithFirestore()
        repository.listenForFirestoreChangesInFavoriteRecipes()
    }

    private fun observeRoomChangesAndSyncWithFirestore() {
        viewModelScope.launch {
            recipes.collect { recipes ->
                syncWithFirestore(recipes)
            }
        }
    }

    private fun syncWithFirestore(roomRecipes: List<FavoriteRecipe>) {
        val firestore = FirebaseFirestore.getInstance()
        val userId = AppUser.instance?.userId!!
        val favoriteRecipesCollection =  firestore.collection("users").document(userId).collection("Favorite Recipes")

        favoriteRecipesCollection.get().addOnSuccessListener { snapshot ->
            val firestoreRecipes = snapshot.documents.map { document ->
                document.toObject(FavoriteRecipe::class.java)!!
            }

            val recipesToAddOrUpdate = roomRecipes.filter { recipe ->
                !firestoreRecipes.contains(recipe)  // Recipes that are new or updated
            }

            val recipesToDelete = firestoreRecipes.filter { recipe ->
                !roomRecipes.contains(recipe)  // Recipes that are removed in Room
            }

            // 1. Add or update recipes
            recipesToAddOrUpdate.forEach { recipe ->
                favoriteRecipesCollection.document(recipe.id.toString()).set(recipe)
            }

            // 2. Delete removed recipes
            recipesToDelete.forEach { recipe ->
                favoriteRecipesCollection.document(recipe.id.toString()).delete()
            }
        }
    }
}