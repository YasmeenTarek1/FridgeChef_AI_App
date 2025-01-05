package com.example.recipeapp.ui.feedFragment

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.recipeapp.AppUser
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.ExtraDetailsResponse
import com.example.recipeapp.api.model.Recipe
import com.example.recipeapp.sharedPreferences.SharedPreferences
import kotlinx.coroutines.flow.first

class SimilarRecipesPagingSource(
    private val repository: Repository,
    private val sharedPreferences: SharedPreferences
) : PagingSource<Int, Recipe>() {

    private val takenFromFavRecipesIDs: MutableSet<Int> = sharedPreferences.getTakenIDs().toMutableSet()

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Recipe> {
        return try {
            val page = params.key ?: 1
            var taken = false
            var recipeId = 0

            var results: MutableSet<Recipe> = mutableSetOf()
            val favoriteRecipes = repository.getAllFavoriteRecipes().first()
            val cookedRecipes = repository.getAllCookedRecipes().first()
            val recipesToFilterOut: MutableSet<Int> = mutableSetOf()

            // Get a new favorite recipe ID that hasn't been used
            for (favoriteRecipe in favoriteRecipes) {
                if (!taken && !takenFromFavRecipesIDs.contains(favoriteRecipe.id)) {
                    takenFromFavRecipesIDs.add(favoriteRecipe.id)
                    taken = true
                    recipeId = favoriteRecipe.id
                }
                recipesToFilterOut.add(favoriteRecipe.id)
            }
            for(cookedRecipe in cookedRecipes){
                recipesToFilterOut.add(cookedRecipe.id)
            }

            // Get user's diet type, ignore "balanced"
            var diet: String? = repository.getUserById(AppUser.instance!!.userId!!)?.dietType?.lowercase()
            if (diet == "balanced") diet = null

            // Fetch similar recipes to a fav one
            if (taken) {
                results.addAll(repository.getSimilarRecipes(recipeId = recipeId).toList())
                results = results.filterNot { recipesToFilterOut.contains(it.id) }.toMutableSet()

                // Fill recipe image if missing (in case of similar recipes to a fav - Image not included int the api response)
                results.forEach { recipe ->
                    val detailedRecipe: ExtraDetailsResponse = repository.getRecipeInfo(recipe.id)
                    if(detailedRecipe.image != null)
                        recipe.image = detailedRecipe.image

                }
            }

            // Fetch random recipes if no fav recipes available or similar recipes are insufficient
            while(results.size < 8) {
                try {
                    var additionalRecipes = repository.getRandomRecipes(diet = diet).toMutableList()
                    additionalRecipes = additionalRecipes.filterNot { recipesToFilterOut.contains(it.id) }.toMutableList()
                    results.addAll(additionalRecipes)
                } catch (e: Exception) {
                    if (e.message?.contains("402") == true) {
                        Log.e("RecipeFetchError", "Spoonacular Api limit reached: ${e.message}")
                        break
                    }
                    if (e.message?.contains("429") == true) {
                        Log.e("RecipeFetchError", "Gemini Api limit reached: ${e.message}")
                        break
                    }
                }
            }

            // Save the IDs in shared preferences
            sharedPreferences.saveTakenIDs(takenFromFavRecipesIDs)
            Log.d("PagingSource"," final result size is ${results.size} ");

            LoadResult.Page(
                data = results.toList(),
                prevKey = if (page == 1) null else page - 1, // Handle first page
                nextKey = if (results.isEmpty()) null else page + 1 // Continue loading if non-empty response
            )
        } catch (e: Exception) {
            Log.e("PagingSource", "Error loading data", e)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Recipe>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}