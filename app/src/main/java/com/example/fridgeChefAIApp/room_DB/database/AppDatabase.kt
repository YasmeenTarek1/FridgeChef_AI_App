package com.example.fridgeChefAIApp.room_DB.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fridgeChefAIApp.room_DB.dao.AiRecipesDao
import com.example.fridgeChefAIApp.room_DB.dao.CookedRecipesDao
import com.example.fridgeChefAIApp.room_DB.dao.FavoriteRecipesDao
import com.example.fridgeChefAIApp.room_DB.dao.ToBuyIngredientsDao
import com.example.fridgeChefAIApp.room_DB.dao.UserDao
import com.example.fridgeChefAIApp.room_DB.model.AiRecipe
import com.example.fridgeChefAIApp.room_DB.model.CookedRecipe
import com.example.fridgeChefAIApp.room_DB.model.FavoriteRecipe
import com.example.fridgeChefAIApp.room_DB.model.ToBuyIngredient
import com.example.fridgeChefAIApp.room_DB.model.UserInfo

@Database(entities = [CookedRecipe::class, FavoriteRecipe::class, ToBuyIngredient::class , AiRecipe::class , UserInfo::class], version = 32 , exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cookedRecipesDao(): CookedRecipesDao
    abstract fun favoriteRecipesDao(): FavoriteRecipesDao
    abstract fun toBuyIngredientsDao(): ToBuyIngredientsDao
    abstract fun aiRecipesDao(): AiRecipesDao
    abstract fun userDao(): UserDao
}
