package com.example.fridgeChefAIApp.ui.loginFragments

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgeChefAIApp.AppUser
import com.example.fridgeChefAIApp.Repository
import com.example.fridgeChefAIApp.room_DB.model.AiRecipe
import com.example.fridgeChefAIApp.room_DB.model.CookedRecipe
import com.example.fridgeChefAIApp.room_DB.model.FavoriteRecipe
import com.example.fridgeChefAIApp.room_DB.model.ToBuyIngredient
import com.example.fridgeChefAIApp.room_DB.model.UserInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class FetchingViewModel (private val repository: Repository) : ViewModel() {

    suspend fun getUserName(): String {
        return repository.getUserById(AppUser.instance!!.userId!!)!!.name.substringBefore(" ")
    }

    suspend fun fetchUserInfoFromFirestoreAndInitializeRoom(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val userId: String = AppUser.instance!!.userId!!
                val firestore = FirebaseFirestore.getInstance()
                val documentRef = firestore.collection("users").document(userId)

                val documentSnapshot = documentRef.get().await()
                if (!documentSnapshot.exists()) {
                    // Document does not exist
                    false
                } else {
                    // Document exists, proceed to fetch and save user data
                    fetchAndSaveUserData(firestore, userId)
                    true
                }
            } catch (e: Exception) {
                // Log the error and return false to indicate failure
                Log.e("Firestore", "Error fetching user info: ${e.message}")
                false
            }
        }
    }


    private fun fetchAndSaveUserData(firestore: FirebaseFirestore, userId: String) {

        // fetching UserInfo
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val userInfo: UserInfo? = documentSnapshot.toObject(UserInfo::class.java)
                    userInfo?.let {
                        // Sync data with local Room database
                        viewModelScope.launch {
                            if (repository.getUserById(userId) == null) {
                                // NOT saved before to my Room DB so insert it
                                repository.insertUser(userInfo)
                            } else {
                                // just update the one saved in the Room DB
                                repository.updateUser(userInfo)
                            }
                        }
                    }
                }
            }

        // fetching Favorite Recipes
        firestore.collection("users").document(userId)
            .collection("Favorite Recipes") // sub Collection
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Convert the documents to FavoriteRecipe objects
                val favoriteRecipes = querySnapshot.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject<FavoriteRecipe>()
                }

                viewModelScope.launch {
                    repository.insertFavoriteRecipes(favoriteRecipes)
                }

            }
            .addOnFailureListener { exception ->
                Log.w("Error", "Error in fetching fav Recipes from Firestore", exception)
            }


        // fetching Cooked Recipes
        firestore.collection("users").document(userId)
            .collection("Cooked Recipes") // sub Collection
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Convert the documents to FavoriteRecipe objects
                val cookedRecipes = querySnapshot.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject<CookedRecipe>()
                }

                viewModelScope.launch {
                    repository.insertCookedRecipes(cookedRecipes)
                }

            }
            .addOnFailureListener { exception ->
                Log.w("Error", "Error in fetching cooked Recipes from Firestore", exception)
            }


        // fetching To-Buy Ingredients
        firestore.collection("users").document(userId)
            .collection("To-Buy Ingredients") // sub Collection
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Convert the documents to FavoriteRecipe objects
                val ingredients = querySnapshot.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject<ToBuyIngredient>()
                }

                viewModelScope.launch {
                    repository.insertToBuyIngredients(ingredients)
                }

            }
            .addOnFailureListener { exception ->
                Log.w("Error", "Error in fetching To-Buy Ingredients from Firestore", exception)
            }

        // fetching Ai Recipes
        firestore.collection("users").document(userId)
            .collection("Ai Recipes") // sub Collection
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Convert the documents to Ai Recipes objects
                val aiRecipes = querySnapshot.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject<AiRecipe>()
                }

                viewModelScope.launch {
                    repository.insertAiRecipes(aiRecipes)
                }

            }
            .addOnFailureListener { exception ->
                Log.w("Error", "Error in fetching Ai Recipes from Firestore", exception)
            }

    }
}
