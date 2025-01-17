package com.example.fridgeChefAIApp.room_DB.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fridgeChefAIApp.room_DB.model.AiRecipe
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

    @Query("DELETE FROM ai_recipes WHERE id = :recipeId")
    suspend fun deleteAiRecipe(recipeId: Int)

    @Query("SELECT ingredients FROM ai_recipes WHERE id = :recipeId")
    suspend fun getAiRecipeIngredients(recipeId: Int): String

    @Query("SELECT steps FROM ai_recipes WHERE id = :recipeId")
    suspend fun getAiRecipeSteps(recipeId: Int): String

    @Query("SELECT id FROM ai_recipes ORDER BY id DESC LIMIT 1")
    suspend fun getLastInsertedAiRecipeID(): Int
}