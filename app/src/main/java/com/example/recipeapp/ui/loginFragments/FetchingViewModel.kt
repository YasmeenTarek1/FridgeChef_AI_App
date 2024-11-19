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
import com.example.recipeapp.room_DB.model.UserInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.launch


class FetchingViewModel (private val repository: Repository) : ViewModel() {

    fun fetchUserInfoFromFirestoreAndInitializeRoom(): Boolean{
        val userId: String = AppUser.instance!!.userId!!
        val firestore = FirebaseFirestore.getInstance()
        var success = false

        val documentRef = firestore.collection("users").document(userId)

        documentRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                // Document exists
                // fetching User Info
                success = true
                firestore.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val userInfo: UserInfo? =
                                documentSnapshot.toObject(UserInfo::class.java)
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
                        val favoriteRecipes =
                            querySnapshot.documents.mapNotNull { documentSnapshot ->
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
                        // Convert the documents to CookedRecipe objects
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
                        // Convert the documents to ToBuyIngredient objects
                        val ingredients = querySnapshot.documents.mapNotNull { documentSnapshot ->
                            documentSnapshot.toObject<ToBuyIngredient>()
                        }

                        viewModelScope.launch {
                            repository.insertToBuyIngredients(ingredients)
                        }

                    }
                    .addOnFailureListener { exception ->
                        Log.w(
                            "Error",
                            "Error in fetching To-Buy Ingredients from Firestore",
                            exception
                        )
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
        return success
    }
}
