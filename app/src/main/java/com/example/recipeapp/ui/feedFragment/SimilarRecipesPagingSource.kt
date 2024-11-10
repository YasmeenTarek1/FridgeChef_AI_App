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

    private val takenIDs: MutableSet<Int> = sharedPreferences.getTakenIDs().toMutableSet()
    private val currentRecipesIDs: MutableSet<Int> = sharedPreferences.getCurrentRecipesIDs().toMutableSet()

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Recipe> {
        return try {
            val page = params.key ?: 1
            var taken = false
            var recipeId = 0

            // Collect all favorite recipes
            val favoriteRecipes = repository.getAllFavoriteRecipes().first()
            currentRecipesIDs.addAll(favoriteRecipes.map { it.id })

            // Get a new favorite recipe ID that hasn't been used
            for (favorite in favoriteRecipes) {
                if (!takenIDs.contains(favorite.id)) {
                    takenIDs.add(favorite.id)
                    taken = true
                    recipeId = favorite.id
                    break
                }
            }

            var response: MutableList<Recipe> = mutableListOf()
            var moreRecipesNeeded = false

            // Get user's diet type, ignore "balanced"
            var diet: String? = repository.getUserById(AppUser.instance!!.userId!!)?.dietType?.lowercase()
            if (diet == "balanced") diet = null

            // Fetch similar recipes or fallback to random recipes if needed
            if (taken) {
                val recipes = repository.getSimilarRecipes(recipeId = recipeId).toMutableList()
                response = recipes.filterNot { currentRecipesIDs.contains(it.id) }.toMutableList()

                // Fill recipe details
                response.forEach { recipe ->
                    val detailedRecipe: ExtraDetailsResponse = repository.getRecipeInfo(recipe.id)
                    val chatBotServiceViewModel = ChatBotServiceViewModel(repository)
                    if (detailedRecipe.image != null) {
                        recipe.image = detailedRecipe.image
                    }
                    recipe.summary = chatBotServiceViewModel.summarizeSummary(detailedRecipe.summary)
                }

                // Filter out recipes with missing images
                response = response.filterNot { it.image == null }.toMutableList()

                // Check if we have enough recipes, else fetch additional random ones
                if (response.size < params.loadSize) {
                    moreRecipesNeeded = true
                }
            } else {
                moreRecipesNeeded = true
            }

            // Fetch random recipes if similar recipes are insufficient
            if (moreRecipesNeeded) {
                val additionalRecipes = repository.getRandomRecipes(diet = diet).toMutableList()
                additionalRecipes.forEach { recipe ->
                    val chatBotServiceViewModel = ChatBotServiceViewModel(repository)
                    recipe.summary = chatBotServiceViewModel.summarizeSummary(recipe.summary)
                }
                response.addAll(additionalRecipes)
            }

            // Save the IDs in shared preferences
            currentRecipesIDs.addAll(response.map { it.id })
            sharedPreferences.saveTakenIDs(takenIDs)
            sharedPreferences.saveCurrentRecipesIDs(currentRecipesIDs)

            LoadResult.Page(
                data = response,
                prevKey = if (page == 1) null else page - 1, // Handle first page
                nextKey = if (response.isEmpty()) null else page + 1 // Continue loading if non-empty response
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