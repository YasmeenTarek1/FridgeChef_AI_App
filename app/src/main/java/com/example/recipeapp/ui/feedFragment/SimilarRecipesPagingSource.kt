package com.example.recipeapp.ui.feedFragment

import android.content.Context
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.recipeapp.AppUser
import com.example.recipeapp.api.model.DetailedRecipeResponse
import com.example.recipeapp.api.model.Recipe
import com.example.recipeapp.api.service.ApiService
import com.example.recipeapp.room_DB.dao.FavoriteRecipesDao
import com.example.recipeapp.room_DB.database.AppDatabase
import kotlinx.coroutines.flow.first
import kotlin.random.Random

class SimilarRecipesPagingSource(private val api: ApiService, private val favoriteRecipesDao: FavoriteRecipesDao , private val context: Context) : PagingSource<Int, Recipe>() {

    private val takenIDs = mutableSetOf<Int>()

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Recipe> {
        return try {
            val page = params.key ?: 1
            var taken = false
            var recipeId = 0

            // only the first value of the fav recipes will be collected and then the flow is shut down (Cold Stream)
            val favoriteRecipes = favoriteRecipesDao.getAllFavoriteRecipes().first()

            for(i in favoriteRecipes.indices){ // get another fav recipe that wasn't handled before, otherwise go get random recipes
                if(!takenIDs.contains(favoriteRecipes.get(i).id)){
                    takenIDs.add(favoriteRecipes.get(i).id)
                    taken = true
                    recipeId = favoriteRecipes.get(i).id
                    break
                }
            }

            var response: MutableList<Recipe> = mutableListOf()
            if (!taken) {
                var diet:String? = AppDatabase.getInstance(context)!!.userDao().getUserById(AppUser.instance!!.userId!!)!!.dietType.lowercase()
                if(diet == "balanced")
                    diet = null
                response = api.getRandomRecipes(diet = diet).recipes.toMutableList()
            } else {
                var recipes: List<Recipe> = api.getSimilarRecipes(recipeId = recipeId)
                for(recipe in recipes){
                    val detailedRecipe: DetailedRecipeResponse = api.getRecipeInfo(recipe.id)
                    recipe.image = detailedRecipe.image
                    recipe.likes = Random.nextInt(1, 11)
                    recipe.healthScore = detailedRecipe.healthScore.toDouble()
                }
                response = recipes.toMutableList()
            }
            LoadResult.Page(
                data = response,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            Log.e("PagingSource", "Error loading data", e)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Recipe>): Int? {
        return null
    }
}
