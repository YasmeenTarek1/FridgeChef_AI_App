package com.example.fridgeChefAIApp.room_DB.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fridgeChefAIApp.room_DB.model.ToBuyIngredient
import kotlinx.coroutines.flow.Flow

@Dao
interface ToBuyIngredientsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToBuyIngredient(toBuyIngredient: ToBuyIngredient)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToBuyIngredients(toBuyIngredients: List<ToBuyIngredient>)

    @Query("SELECT * FROM to_buy_ingredients ORDER BY createdAt DESC")
    fun getAllToBuyIngredients(): Flow<List<ToBuyIngredient>>

    @Query("DELETE FROM to_buy_ingredients")
    suspend fun clearAll()

    @Delete
    suspend fun deleteIngredient(ingredient: ToBuyIngredient)
}
