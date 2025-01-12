package com.example.recipeapp

import android.content.Context
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.recipeapp.api.model.ExtraDetailsResponse
import com.example.recipeapp.api.model.Ingredient
import com.example.recipeapp.api.model.Recipe
import com.example.recipeapp.api.model.Step
import com.example.recipeapp.api.service.ApiService
import com.example.recipeapp.api.service.RetrofitInstance
import com.example.recipeapp.room_DB.dao.AiRecipesDao
import com.example.recipeapp.room_DB.dao.CookedRecipesDao
import com.example.recipeapp.room_DB.dao.FavoriteRecipesDao
import com.example.recipeapp.room_DB.dao.ToBuyIngredientsDao
import com.example.recipeapp.room_DB.dao.UserDao
import com.example.recipeapp.room_DB.database.AppDatabase
import com.example.recipeapp.room_DB.model.AiRecipe
import com.example.recipeapp.room_DB.model.CookedRecipe
import com.example.recipeapp.room_DB.model.FavoriteRecipe
import com.example.recipeapp.room_DB.model.ToBuyIngredient
import com.example.recipeapp.room_DB.model.UserInfo
import com.example.recipeapp.sharedPreferences.SharedPreferences
import com.example.recipeapp.ui.feedFragment.SimilarRecipesPagingSource
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

    private val api: ApiService = retrofitInstance.getApiService(BASE_URL = "https://api.spoonacular.com/")
    private val customSearchApi: ApiService = retrofitInstance.getApiService(BASE_URL = "https://www.googleapis.com/")
    private val cookedRecipesDao: CookedRecipesDao = dataBase!!.cookedRecipesDao()
    private val favoriteRecipesDao: FavoriteRecipesDao = dataBase!!.favoriteRecipesDao()
    private val toBuyIngredientsDao: ToBuyIngredientsDao = dataBase!!.toBuyIngredientsDao()
    private val aiRecipesDao: AiRecipesDao = dataBase!!.aiRecipesDao()
    private val userDao: UserDao = dataBase!!.userDao()

    private val cookedRecipes: Flow<List<CookedRecipe>> = getAllCookedRecipes()
    private val favRecipes: Flow<List<FavoriteRecipe>> = getAllFavoriteRecipes()
    private val aiRecipes: Flow<List<AiRecipe>> = getAllAiRecipes()
    private val toBuyIngredients: Flow<List<ToBuyIngredient>> = getAllToBuyIngredients()


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
    fun getSimilarRecipesForFavorites(repository: Repository , context: Context): Flow<PagingData<Recipe>> {
        return Pager(
            config = PagingConfig(pageSize = 5, enablePlaceholders = false),
            pagingSourceFactory = { SimilarRecipesPagingSource(repository , SharedPreferences(context)) }
        ).flow
    }

    suspend fun autocompleteRecipeSearch(query: String): List<Recipe> {
        return try {
            api.autocompleteRecipeSearch(query = query)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRecipeInfo(recipeId: Int): ExtraDetailsResponse {
        return try {
            api.getRecipeInfo(recipeId = recipeId)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getRecipeIngredients(recipeId: Int): List<Ingredient> {
        return try {
            api.getIngredients(recipeId = recipeId).ingredients
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
        maxCarbs: Int? = null, maxProtein: Int? = null, maxFat: Int? = null, maxCalories: Int? = null, maxSugar: Int? = null)
    : List<Recipe> {
        return try {
            api.searchRecipesByNutrients(maxCarbs = maxCarbs, maxProtein = maxProtein, maxSugar = maxSugar, maxFat = maxFat, maxCalories = maxCalories)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRecipeOrIngredientImage(title: String): String{
        return try {
            customSearchApi.getRecipeOrIngredientImage(title = title).items!!.get(0).link
        } catch (e: Exception) {
            throw e
        }
    }

    // Database-related functions for CookedRecipes

    suspend fun insertCookedRecipe(cookedRecipe: CookedRecipe) = cookedRecipesDao.insertCookedRecipe(cookedRecipe)

    suspend fun insertCookedRecipes(cookedRecipes: List<CookedRecipe>) = cookedRecipesDao.insertCookedRecipes(cookedRecipes)

    suspend fun clearAllCookedRecipes() = cookedRecipesDao.clearAll()

    fun getAllCookedRecipes(): Flow<List<CookedRecipe>> = cookedRecipesDao.getAllCookedRecipes()

    suspend fun deleteCookedRecipe(recipeID: Int) = cookedRecipesDao.deleteCookedRecipe(recipeID)

    fun isCookedRecipeExists(recipeId : Int) = cookedRecipesDao.isCookedRecipeExists(recipeId)

    // Database-related functions for FavoriteRecipes

    suspend fun insertFavoriteRecipe(favoriteRecipe: FavoriteRecipe) = favoriteRecipesDao.insertFavoriteRecipe(favoriteRecipe)

    suspend fun insertFavoriteRecipes(favoriteRecipes: List<FavoriteRecipe>) = favoriteRecipesDao.insertFavoriteRecipes(favoriteRecipes)

    fun getAllFavoriteRecipes(): Flow<List<FavoriteRecipe>> = favoriteRecipesDao.getAllFavoriteRecipes()

    suspend fun clearAllToFavoriteRecipes() = favoriteRecipesDao.clearAll()

    suspend fun deleteFavoriteRecipe(favoriteRecipeId: Int) = favoriteRecipesDao.deleteFavoriteRecipe(favoriteRecipeId)

    fun isFavoriteRecipeExists(recipeId : Int) = favoriteRecipesDao.isFavoriteRecipeExists(recipeId)

    fun updateFavoriteRecipe(recipeId: Int, readyInMinutes: Int, servings: Int, summary: String) = favoriteRecipesDao.updateFavoriteRecipe(recipeId, readyInMinutes, servings, summary)

    // Database-related functions for ToBuyIngredients

    suspend fun insertToBuyIngredient(toBuyIngredient: ToBuyIngredient) = toBuyIngredientsDao.insertToBuyIngredient(toBuyIngredient)

    suspend fun insertToBuyIngredients(toBuyIngredients: List<ToBuyIngredient>) = toBuyIngredientsDao.insertToBuyIngredients(toBuyIngredients)

    suspend fun clearAllToBuyIngredients() = toBuyIngredientsDao.clearAll()

    fun getAllToBuyIngredients(): Flow<List<ToBuyIngredient>> = toBuyIngredientsDao.getAllToBuyIngredients()

    suspend fun deleteIngredient(toBuyIngredient: ToBuyIngredient) = toBuyIngredientsDao.deleteIngredient(toBuyIngredient)

    // Database-related functions for AiRecipes

    suspend fun insertAiRecipe(aiRecipe: AiRecipe) = aiRecipesDao.insertAiRecipe(aiRecipe)

    suspend fun insertAiRecipes(aiRecipes: List<AiRecipe>) = aiRecipesDao.insertAiRecipes(aiRecipes)

    suspend fun clearAllAiRecipes() = aiRecipesDao.clearAll()

    fun getAllAiRecipes(): Flow<List<AiRecipe>> = aiRecipesDao.getAllAiRecipes()

    suspend fun deleteAiRecipe(recipeID: Int) = aiRecipesDao.deleteAiRecipe(recipeID)

    suspend fun getAiRecipeIngredients(recipeId: Int) = aiRecipesDao.getAiRecipeIngredients(recipeId)

    suspend fun getAiRecipeSteps(recipeId: Int) = aiRecipesDao.getAiRecipeSteps(recipeId)

    suspend fun getLastInsertedAiRecipeID() = aiRecipesDao.getLastInsertedAiRecipeID()

    // User-related functions

    suspend fun insertUser(userInfo: UserInfo) = userDao.insertUser(userInfo)

    suspend fun updateUser(userInfo: UserInfo) = userDao.updateUser(userInfo)

    suspend fun getUserById(userId: String): UserInfo? = userDao.getUserById(userId)

    // Listening for Firestore changes

    suspend fun listenForFirestoreChangesInCookedRecipes() {
        cookedRecipes.collect { cookedRecipes ->
            updateCookedRecipesInFirestore(cookedRecipes)
        }

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
                                cookedRecipesDao.deleteCookedRecipe(removedRecipe.id)
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

    suspend fun listenForFirestoreChangesInFavoriteRecipes() {
        favRecipes.collect { favRecipes ->
            updateFavRecipesInFirestore(favRecipes)
        }

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
                                favoriteRecipesDao.deleteFavoriteRecipe(removedRecipe.id)
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

    suspend fun listenForFirestoreChangesInToBuyIngredients() {
        toBuyIngredients.collect { toBuyIngredients ->
            updateToBuyIngredientsInFirestore(toBuyIngredients)
        }

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
                            val ingredient = documentChange.document.toObject(ToBuyIngredient::class.java)
                            updatedIngredients.add(ingredient)
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

    suspend fun listenForFirestoreChangesInAiRecipes() {
        aiRecipes.collect { aiRecipes ->
            updateAiRecipesInFirestore(aiRecipes)
        }

        val firestore = FirebaseFirestore.getInstance()
        val userDocRef = firestore.collection("users").document(AppUser.instance!!.userId!!)
        val recipesCollection = userDocRef.collection("Ai Recipes")

        // Listen for changes in Firestore
        recipesCollection.addSnapshotListener { snapshots, e ->
            if (e != null) {
                // Handle error
                return@addSnapshotListener
            }

            if (snapshots != null) {
                val updatedRecipes = mutableListOf<AiRecipe>()

                for (documentChange in snapshots.documentChanges) {
                    when (documentChange.type) {
                        DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED -> {
                            // If an ingredient is added or modified, add it to the list
                            val recipe = documentChange.document.toObject(AiRecipe::class.java)
                            updatedRecipes.add(recipe)
                        }
                        DocumentChange.Type.REMOVED -> {
                            // If an ingredient is removed in Firestore, delete it from Room
                            val removedRecipe = documentChange.document.toObject(AiRecipe::class.java)
                            CoroutineScope(Dispatchers.IO).launch {
                                aiRecipesDao.deleteAiRecipe(removedRecipe.id)
                            }
                        }
                    }
                }

                CoroutineScope(Dispatchers.IO).launch {
                    aiRecipesDao.insertAiRecipes(updatedRecipes)
                }
            }
        }
    }

    // Saving new info added in Room DB to firestore

    fun updateFavRecipesInFirestore(roomRecipes: List<FavoriteRecipe>) {
        val firestore = FirebaseFirestore.getInstance()
        val userId = AppUser.instance?.userId ?: return
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

    fun updateCookedRecipesInFirestore(roomRecipes: List<CookedRecipe>) {
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

    fun updateAiRecipesInFirestore(roomRecipes: List<AiRecipe>) {
        val firestore = FirebaseFirestore.getInstance()
        val userId = AppUser.instance?.userId!!
        val aiRecipesCollection =  firestore.collection("users").document(userId).collection("Ai Recipes")

        aiRecipesCollection.get().addOnSuccessListener { snapshot ->
            val firestoreRecipes = snapshot.documents.map { document ->
                document.toObject(AiRecipe::class.java)!!
            }

            val recipesToAddOrUpdate = roomRecipes.filter { recipe ->
                !firestoreRecipes.contains(recipe)  // Recipes that are new or updated
            }

            val recipesToDelete = firestoreRecipes.filter { recipe ->
                !roomRecipes.contains(recipe)  // Recipes that are removed in Room
            }

            // 1. Add or update recipes
            recipesToAddOrUpdate.forEach { recipe ->
                aiRecipesCollection.document(recipe.id.toString()).set(recipe)
            }

            // 2. Delete removed recipes
            recipesToDelete.forEach { recipe ->
                aiRecipesCollection.document(recipe.id.toString()).delete()
            }
        }
    }

    fun updateToBuyIngredientsInFirestore(roomIngredients: List<ToBuyIngredient>) {
        val firestore = FirebaseFirestore.getInstance()
        val userId = AppUser.instance?.userId!!
        val toBuyIngredientsCollection =  firestore.collection("users").document(userId).collection("To-Buy Ingredients")

        toBuyIngredientsCollection.get().addOnSuccessListener { snapshot ->
            val firestoreIngredients = snapshot.documents.map { document ->
                document.toObject(ToBuyIngredient::class.java)!!
            }

            val ingredientsToAddOrUpdate = roomIngredients.filter { ingredient ->
                !firestoreIngredients.contains(ingredient)  // Ingredients that are new or updated
            }

            val ingredientsToDelete = firestoreIngredients.filter { ingredient ->
                !roomIngredients.contains(ingredient)  // Ingredients that are removed in Room
            }

            // 1. Add or update ingredients
            ingredientsToAddOrUpdate.forEach { ingredient ->
                toBuyIngredientsCollection.document(ingredient.name.toString()).set(ingredient)
            }

            // 2. Delete removed ingredients
            ingredientsToDelete.forEach { ingredient ->
                toBuyIngredientsCollection.document(ingredient.name.toString()).delete()
            }
        }
    }
}
