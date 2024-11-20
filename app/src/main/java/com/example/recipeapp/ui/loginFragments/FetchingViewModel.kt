package com.example.recipeapp.ui.loginFragments

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.AppUser
import com.example.recipeapp.Repository
import com.example.recipeapp.room_DB.model.AiRecipe
import com.example.recipeapp.room_DB.model.CookedRecipe
import com.example.recipeapp.room_DB.model.FavoriteRecipe
import com.example.recipeapp.room_DB.model.ToBuyIngredient
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.suspendCoroutine


class FetchingViewModel (private val repository: Repository) : ViewModel() {

    suspend fun fetchUserInfoFromFirestoreAndInitializeRoom(): Boolean {
        return withContext(Dispatchers.IO) {
            val userId: String = AppUser.instance!!.userId!!
            val firestore = FirebaseFirestore.getInstance()

            try {
                val documentSnapshot = getDocumentSnapshot(firestore, userId)
                if (documentSnapshot.exists()) {
                    // Document exists, proceed to fetch and update data
                    fetchAndSaveUserData(firestore, userId)
                    true // Operation was successful
                } else {
                    Log.w("FetchingViewModel", "Document does not exist for userId: $userId")
                    false
                }
            } catch (e: Exception) {
                Log.e("FetchingViewModel", "Error fetching user data", e)
                false
            }
        }
    }

    private suspend fun getDocumentSnapshot(firestore: FirebaseFirestore, userId: String) =
        suspendCoroutine<DocumentSnapshot> { continuation ->
            val documentRef = firestore.collection("users").document(userId)
            documentRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    continuation.resumeWith(Result.success(documentSnapshot))
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWith(Result.failure(exception))
                }
        }

    private suspend fun fetchAndSaveUserData(firestore: FirebaseFirestore, userId: String) {
        val favoriteRecipes = fetchSubCollection<FavoriteRecipe>(firestore, userId, "Favorite Recipes")
        val cookedRecipes = fetchSubCollection<CookedRecipe>(firestore, userId, "Cooked Recipes")
        val toBuyIngredients = fetchSubCollection<ToBuyIngredient>(firestore, userId, "To-Buy Ingredients")
        val aiRecipes = fetchSubCollection<AiRecipe>(firestore, userId, "Ai Recipes")

        // Save data to local Room database
        viewModelScope.launch {
            repository.insertFavoriteRecipes(favoriteRecipes)
            repository.insertCookedRecipes(cookedRecipes)
            repository.insertToBuyIngredients(toBuyIngredients)
            repository.insertAiRecipes(aiRecipes)
        }
    }

    private suspend inline fun <reified T> fetchSubCollection(
        firestore: FirebaseFirestore,
        userId: String,
        collectionName: String
    ): List<T> = suspendCoroutine { continuation ->
        firestore.collection("users").document(userId).collection(collectionName).get()
            .addOnSuccessListener { querySnapshot ->
                val items = querySnapshot.documents.mapNotNull { it.toObject<T>() }
                continuation.resumeWith(Result.success(items))
            }
            .addOnFailureListener { exception ->
                continuation.resumeWith(Result.failure(exception))
            }
    }
}
