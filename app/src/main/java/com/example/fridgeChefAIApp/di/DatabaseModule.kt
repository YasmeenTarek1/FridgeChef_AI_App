package com.example.fridgeChefAIApp.di

import android.content.Context
import androidx.room.Room
import com.example.fridgeChefAIApp.room_DB.dao.AiRecipesDao
import com.example.fridgeChefAIApp.room_DB.dao.CookedRecipesDao
import com.example.fridgeChefAIApp.room_DB.dao.FavoriteRecipesDao
import com.example.fridgeChefAIApp.room_DB.dao.ToBuyIngredientsDao
import com.example.fridgeChefAIApp.room_DB.dao.UserDao
import com.example.fridgeChefAIApp.room_DB.database.AppDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "recipe_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideFavoriteRecipesDao(database: AppDatabase): FavoriteRecipesDao {
        return database.favoriteRecipesDao()
    }

    @Provides
    fun provideCookedRecipesDao(database: AppDatabase): CookedRecipesDao {
        return database.cookedRecipesDao()
    }

    @Provides
    fun provideAiRecipesDao(database: AppDatabase): AiRecipesDao {
        return database.aiRecipesDao()
    }

    @Provides
    fun provideToBuyIngredientsDao(database: AppDatabase): ToBuyIngredientsDao {
        return database.toBuyIngredientsDao()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
}
