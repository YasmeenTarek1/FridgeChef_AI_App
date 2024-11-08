package com.example.recipeapp.room_DB.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recipeapp.room_DB.model.AiRecipe
import kotlinx.coroutines.flow.Flow

@Dao
interface AiRecipesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiRecipe(aiRecipe: AiRecipe)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiRecipes(cookedRecipes: List<AiRecipe>)

    @Query("SELECT * FROM ai_recipes ORDER BY createdAt DESC")
    fun getAllAiRecipes(): Flow<List<AiRecipe>>

    @Query("DELETE FROM ai_recipes")
    suspend fun clearAll()

    @Delete
    suspend fun deleteAiRecipe(aiRecipe: AiRecipe)

    @Query("SELECT ingredients FROM AI_RECIPES WHERE id = :recipeId")
    suspend fun getAiRecipeIngredients(recipeId: Int): String

    @Query("SELECT steps FROM AI_RECIPES WHERE id = :recipeId")
    suspend fun getAiRecipeSteps(recipeId: Int): String

    @Query("SELECT id FROM ai_recipes ORDER BY id DESC LIMIT 1")
    suspend fun getLastInsertedAiRecipeID(): Int
}