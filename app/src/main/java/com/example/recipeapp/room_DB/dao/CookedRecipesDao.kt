package com.example.recipeapp.room_DB.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recipeapp.room_DB.model.CookedRecipe
import kotlinx.coroutines.flow.Flow

@Dao
interface CookedRecipesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCookedRecipe(cookedRecipe: CookedRecipe)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCookedRecipes(cookedRecipes: List<CookedRecipe>)

    @Query("SELECT * FROM cooked_recipes ORDER BY createdAt DESC")
    fun getAllCookedRecipes(): Flow<List<CookedRecipe>>

    @Query("DELETE FROM cooked_recipes")
    suspend fun clearAll()

    @Query("DELETE FROM cooked_recipes WHERE id = :recipeId")
    suspend fun deleteCookedRecipe(recipeId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM cooked_recipes WHERE id = :recipeId)")
    fun isCookedRecipeExists(recipeId: Int): Flow<Boolean>
}
