package com.example.recipeapp

import SimilarRecipesPagingSource
import android.content.Context
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.recipeapp.api.model.DetailedRecipeResponse
import com.example.recipeapp.api.model.Ingredient
import com.example.recipeapp.api.model.Recipe
import com.example.recipeapp.api.model.Step
import com.example.recipeapp.api.service.ApiService
import com.example.recipeapp.api.service.RetrofitInstance
import com.example.recipeapp.room_DB.dao.CookedRecipesDao
import com.example.recipeapp.room_DB.dao.FavoriteRecipesDao
import com.example.recipeapp.room_DB.dao.ToBuyIngredientsDao
import com.example.recipeapp.room_DB.dao.UserDao
import com.example.recipeapp.room_DB.database.AppDatabase
import com.example.recipeapp.room_DB.model.CookedRecipe
import com.example.recipeapp.room_DB.model.FavoriteRecipe
import com.example.recipeapp.room_DB.model.ToBuyIngredient
import com.example.recipeapp.room_DB.model.UserInfo
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


class Repository(
    retrofitInstance: RetrofitInstance,
    dataBase: AppDatabase?
) {

    private val api: ApiService = retrofitInstance.getApiService()
    private val cookedRecipesDao: CookedRecipesDao = dataBase!!.cookedRecipesDao()
    private val favoriteRecipesDao: FavoriteRecipesDao = dataBase!!.favoriteRecipesDao()
    private val toBuyIngredientsDao: ToBuyIngredientsDao = dataBase!!.toBuyIngredientsDao()
    private val userDao: UserDao = dataBase!!.userDao()

    // API-related functions

    suspend fun searchRecipesByIngredients(ingredients: List<String>): List<Recipe> {
        return try {
            api.searchRecipesByIngredients(ingredients = ingredients)
        } catch (e: IOException) {
            Log.e("Repository", "Network error: ${e.message}")

            // Handle network errors
            emptyList()
        } catch (e: HttpException) {
            Log.e("Repository", "HTTP error: ${e.message}")

            // Handle HTTP errors
            emptyList()
        }
    }


    suspend fun getSimilarRecipes(recipeId: Int) :List<Recipe>{
        return try {
            api.getSimilarRecipes(recipeId = recipeId)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRandomRecipes(diet: String?) :List<Recipe>{
        return try {
            api.getRandomRecipes(diet = diet).recipes
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getSteps(recipeId: Int): List<Step> {
        return try {
            api.getInstructions(recipeId = recipeId).get(0).steps
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Implementing Paging for similar recipes
    fun getSimilarRecipesForFavorites(context: Context): Flow<PagingData<Recipe>> {
        return Pager(
            config = PagingConfig(pageSize = 5, enablePlaceholders = false),
            pagingSourceFactory = { SimilarRecipesPagingSource(context) }
        ).flow
    }

    suspend fun autocompleteRecipeSearch(query: String): List<Recipe> {
        return try {
            api.autocompleteRecipeSearch(query = query)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRecipeInfo(recipeId: Int): DetailedRecipeResponse {
        return try {
            api.getRecipeInfo(recipeId = recipeId)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun autocompleteIngredientSearch(query: String): List<Ingredient> {
        return try {
            api.autocompleteIngredientSearch(query = query).results
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun searchRecipesByName(query: String): List<Recipe> {
        return try {
            api.searchRecipesByName(query = query).results
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun searchRecipesByNutrients(
        minCarbs: Int? = null, maxCarbs: Int? = null,minProtein: Int? = null,maxProtein: Int? = null,minFat: Int? = null,maxFat: Int? = null,
        minCalories: Int? = null,maxCalories: Int? = null,minSugar: Int? = null, maxSugar: Int? = null)
    : List<Recipe> {

        return try {
            api.searchRecipesByNutrients(minCarbs = minCarbs, maxCarbs = maxCarbs , minProtein = minProtein, maxProtein = maxProtein, minSugar = minSugar, maxSugar = maxSugar, minFat = minFat, maxFat = maxFat, minCalories = minCalories, maxCalories = maxCalories)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Database-related functions for CookedRecipes

    suspend fun insertCookedRecipe(cookedRecipe: CookedRecipe) = cookedRecipesDao.insertCookedRecipe(cookedRecipe)

    suspend fun insertCookedRecipes(cookedRecipes: List<CookedRecipe>) = cookedRecipesDao.insertCookedRecipes(cookedRecipes)

    suspend fun clearAllCookedRecipes() = cookedRecipesDao.clearAll()

    fun getAllCookedRecipes(): Flow<List<CookedRecipe>> = cookedRecipesDao.getAllCookedRecipes()

    suspend fun deleteCookedRecipe(cookedRecipe: CookedRecipe) = cookedRecipesDao.deleteCookedRecipe(cookedRecipe)

    // Database-related functions for FavoriteRecipes

    suspend fun insertFavoriteRecipe(favoriteRecipe: FavoriteRecipe) = favoriteRecipesDao.insertFavoriteRecipe(favoriteRecipe)

    suspend fun insertFavoriteRecipes(favoriteRecipes: List<FavoriteRecipe>) = favoriteRecipesDao.insertFavoriteRecipes(favoriteRecipes)

    fun getAllFavoriteRecipes(): Flow<List<FavoriteRecipe>> = favoriteRecipesDao.getAllFavoriteRecipes()

    suspend fun clearAllToFavoriteRecipes() = favoriteRecipesDao.clearAll()

    suspend fun deleteFavoriteRecipe(favoriteRecipe: FavoriteRecipe) = favoriteRecipesDao.deleteFavoriteRecipe(favoriteRecipe)

    suspend fun isFavoriteRecipeExists(recipeId : Int) = favoriteRecipesDao.isFavoriteRecipeExists(recipeId)

    // Database-related functions for ToBuyIngredients

    suspend fun insertToBuyIngredient(toBuyIngredient: ToBuyIngredient) = toBuyIngredientsDao.insertToBuyIngredient(toBuyIngredient)

    suspend fun insertToBuyIngredients(toBuyIngredients: List<ToBuyIngredient>) = toBuyIngredientsDao.insertToBuyIngredients(toBuyIngredients)

    suspend fun clearAllToBuyIngredients() = toBuyIngredientsDao.clearAll()

    fun getAllToBuyIngredients(): Flow<List<ToBuyIngredient>> = toBuyIngredientsDao.getAllToBuyIngredients()

    suspend fun deleteIngredient(toBuyIngredient: ToBuyIngredient) = toBuyIngredientsDao.deleteIngredient(toBuyIngredient)

    // User-related functions

    suspend fun insertUser(userInfo: UserInfo) = userDao.insertUser(userInfo)

    suspend fun updateUser(userInfo: UserInfo) = userDao.updateUser(userInfo)

    suspend fun getUserById(userId: String): UserInfo? = userDao.getUserById(userId)


    fun listenForFirestoreChangesInCookedRecipes() {
        val firestore = FirebaseFirestore.getInstance()
        val userDocRef = firestore.collection("users").document(AppUser.instance!!.userId!!)
        val cookedRecipesCollection = userDocRef.collection("Cooked Recipes")

        // Listen for changes in Firestore
        cookedRecipesCollection.addSnapshotListener { snapshots, e ->
            if (e != null) {
                // Handle error
                return@addSnapshotListener
            }

            if (snapshots != null) {
                val updatedRecipes = mutableListOf<CookedRecipe>()

                for (documentChange in snapshots.documentChanges) {
                    when (documentChange.type) {
                        DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED -> {
                            // If a recipe is added or modified, add it to the list
                            val recipe = documentChange.document.toObject(CookedRecipe::class.java)
                            updatedRecipes.add(recipe)
                        }
                        DocumentChange.Type.REMOVED -> {
                            // If a recipe is removed in Firestore, delete it from Room
                            val removedRecipe = documentChange.document.toObject(CookedRecipe::class.java)
                            CoroutineScope(Dispatchers.IO).launch {
                                cookedRecipesDao.deleteCookedRecipe(removedRecipe)
                            }
                        }
                    }
                }

                CoroutineScope(Dispatchers.IO).launch {
                    cookedRecipesDao.insertCookedRecipes(updatedRecipes)
                }
            }
        }
    }

    fun listenForFirestoreChangesInFavoriteRecipes() {
        val firestore = FirebaseFirestore.getInstance()
        val userDocRef = firestore.collection("users").document(AppUser.instance!!.userId!!)
        val favoriteRecipesCollection = userDocRef.collection("Favorite Recipes")

        // Listen for changes in Firestore
        favoriteRecipesCollection.addSnapshotListener { snapshots, e ->
            if (e != null) {
                // Handle error
                return@addSnapshotListener
            }

            if (snapshots != null) {
                val updatedRecipes = mutableListOf<FavoriteRecipe>()

                for (documentChange in snapshots.documentChanges) {
                    when (documentChange.type) {
                        DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED -> {
                            // If a recipe is added or modified, add it to the list
                            val recipe = documentChange.document.toObject(FavoriteRecipe::class.java)
                            updatedRecipes.add(recipe)
                        }
                        DocumentChange.Type.REMOVED -> {
                            // If a recipe is removed in Firestore, delete it from Room
                            val removedRecipe = documentChange.document.toObject(FavoriteRecipe::class.java)
                            CoroutineScope(Dispatchers.IO).launch {
                                favoriteRecipesDao.deleteFavoriteRecipe(removedRecipe)
                            }
                        }
                    }
                }

                CoroutineScope(Dispatchers.IO).launch {
                    favoriteRecipesDao.insertFavoriteRecipes(updatedRecipes)
                }
            }
        }
    }

    fun listenForFirestoreChangesInToBuyIngredients() {
        val firestore = FirebaseFirestore.getInstance()
        val userDocRef = firestore.collection("users").document(AppUser.instance!!.userId!!)
        val ingredientsCollection = userDocRef.collection("To-Buy Ingredients")

        // Listen for changes in Firestore
        ingredientsCollection.addSnapshotListener { snapshots, e ->
            if (e != null) {
                // Handle error
                return@addSnapshotListener
            }

            if (snapshots != null) {
                val updatedIngredients = mutableListOf<ToBuyIngredient>()

                for (documentChange in snapshots.documentChanges) {
                    when (documentChange.type) {
                        DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED -> {
                            // If an ingredient is added or modified, add it to the list
                            val recipe = documentChange.document.toObject(ToBuyIngredient::class.java)
                            updatedIngredients.add(recipe)
                        }
                        DocumentChange.Type.REMOVED -> {
                            // If an ingredient is removed in Firestore, delete it from Room
                            val removedIngredient = documentChange.document.toObject(ToBuyIngredient::class.java)
                            CoroutineScope(Dispatchers.IO).launch {
                                toBuyIngredientsDao.deleteIngredient(removedIngredient)
                            }
                        }
                    }
                }

                CoroutineScope(Dispatchers.IO).launch {
                    toBuyIngredientsDao.insertToBuyIngredients(updatedIngredients)
                }
            }
        }
    }
}
