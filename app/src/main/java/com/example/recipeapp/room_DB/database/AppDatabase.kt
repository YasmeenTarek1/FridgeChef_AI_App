package com.example.recipeapp.room_DB.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.recipeapp.room_DB.dao.CookedRecipesDao
import com.example.recipeapp.room_DB.dao.FavoriteRecipesDao
import com.example.recipeapp.room_DB.dao.ToBuyIngredientsDao
import com.example.recipeapp.room_DB.dao.UserDao
import com.example.recipeapp.room_DB.model.CookedRecipe
import com.example.recipeapp.room_DB.model.FavoriteRecipe
import com.example.recipeapp.room_DB.model.ToBuyIngredient
import com.example.recipeapp.room_DB.model.UserInfo

@Database(entities = [CookedRecipe::class, FavoriteRecipe::class, ToBuyIngredient::class , UserInfo::class], version = 10 , exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cookedRecipesDao(): CookedRecipesDao
    abstract fun favoriteRecipesDao(): FavoriteRecipesDao
    abstract fun toBuyIngredientsDao(): ToBuyIngredientsDao
    abstract fun userDao(): UserDao


    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase?{
            var instance = INSTANCE

            if (instance == null) {
                // Synchronize to ensure only one thread creates the instance
                synchronized(this) {
                    // Double-check if INSTANCE is still null inside the synchronized block
                    instance = INSTANCE
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            "recipe_database"
                        ).fallbackToDestructiveMigration().build()
                        INSTANCE = instance
                    }
                }
            }
            return instance
        }
    }
}
