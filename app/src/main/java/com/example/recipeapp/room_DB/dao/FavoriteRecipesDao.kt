package com.example.recipeapp.room_DB.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recipeapp.room_DB.model.FavoriteRecipe
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteRecipesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRecipe(favoriteRecipe: FavoriteRecipe)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRecipes(favoriteRecipes: List<FavoriteRecipe>)

    @Query("DELETE FROM favorite_recipes")
    suspend fun clearAll()

    // Retrieve all favorite recipes
    @Query("SELECT * FROM favorite_recipes ORDER BY createdAt DESC")
    fun getAllFavoriteRecipes(): Flow<List<FavoriteRecipe>>

    @Query("DELETE FROM favorite_recipes WHERE id = :recipeId")
    suspend fun deleteFavoriteRecipe(recipeId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_recipes WHERE id = :recipeId)")
    suspend fun isFavoriteRecipeExists(recipeId: Int): Boolean
}
