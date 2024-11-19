package com.example.recipeapp.ui.feedFragment

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.recipeapp.AppUser
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.ExtraDetailsResponse
import com.example.recipeapp.api.model.Recipe
import com.example.recipeapp.sharedPreferences.SharedPreferences
import com.example.recipeapp.ui.chatBotServiceFragment.ChatBotServiceViewModel
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

            var results: MutableList<Recipe> = mutableListOf()
            var moreRecipesNeeded = false

            // Collect all favorite recipes
            val favoriteRecipes = repository.getAllFavoriteRecipes().first()
            val currentFavRecipesIDs: MutableList<Int> = mutableListOf()

            // Get a new favorite recipe ID that hasn't been used
            for (favorite in favoriteRecipes) {
                if (!taken && !takenFromFavRecipesIDs.contains(favorite.id)) {
                    takenFromFavRecipesIDs.add(favorite.id)
                    taken = true
                    recipeId = favorite.id
                }
                currentFavRecipesIDs.add(favorite.id)
            }

            // Get user's diet type, ignore "balanced"
            var diet: String? = repository.getUserById(AppUser.instance!!.userId!!)?.dietType?.lowercase()
            if (diet == "balanced") diet = null

            // Fetch similar recipes to a fav one
            if (taken) {
                results = repository.getSimilarRecipes(recipeId = recipeId).toMutableList()

                // Fill recipe details
                results.forEach { recipe ->
                    val detailedRecipe: ExtraDetailsResponse = repository.getRecipeInfo(recipe.id)
                    val chatBotServiceViewModel = ChatBotServiceViewModel(repository)
                    if (detailedRecipe.image != null) {
                        recipe.image = detailedRecipe.image
                    }
                    recipe.summary = chatBotServiceViewModel.summarizeSummary(detailedRecipe.summary)
                }

                // Filter out recipes with missing images
                results = results.filterNot { it.image == null }.toMutableList()

                // Check if we have enough recipes, else fetch additional random ones
                if (results.size < 8) {
                    moreRecipesNeeded = true
                }
            }
            else{
                moreRecipesNeeded = true
            }


            // Fetch random recipes if no fav recipes available or similar recipes are insufficient
            if(moreRecipesNeeded){
                var additionalRecipes = repository.getRandomRecipes(diet = diet).toMutableList()
                additionalRecipes = additionalRecipes.filterNot { currentFavRecipesIDs.contains(it.id) }.toMutableList()
                additionalRecipes.forEach { recipe ->
                    val chatBotServiceViewModel = ChatBotServiceViewModel(repository)
                    recipe.summary = chatBotServiceViewModel.summarizeSummary(recipe.summary)
                }
                results.addAll(additionalRecipes)
                results.filterNot { currentFavRecipesIDs.contains(it.id) }.toMutableList()
            }

            // Save the IDs in shared preferences
            sharedPreferences.saveTakenIDs(takenFromFavRecipesIDs)

            LoadResult.Page(
                data = results,
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