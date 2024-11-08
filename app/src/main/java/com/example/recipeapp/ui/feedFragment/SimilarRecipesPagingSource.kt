
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

class SimilarRecipesPagingSource(private val repository: Repository , private val sharedPreferences: SharedPreferences) : PagingSource<Int, Recipe>() {

    private val takenIDs: MutableSet<Int> = sharedPreferences.getTakenIDs().toMutableSet()
    private val currentRecipesIDs: MutableSet<Int> = sharedPreferences.getCurrentRecipesIDs().toMutableSet()

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Recipe> {
        return try {
            val page = params.key ?: 1
            var taken = false
            var recipeId = 0

            // Collect all favorite recipes (Cold Stream)
            val favoriteRecipes = repository.getAllFavoriteRecipes().first()
            currentRecipesIDs.addAll(favoriteRecipes.map { it.id })

            // Get another favorite recipe that wasn't handled before
            for (favorite in favoriteRecipes) {
                if (!takenIDs.contains(favorite.id)) {
                    takenIDs.add(favorite.id)
                    taken = true
                    recipeId = favorite.id
                    break
                }
            }

            var response: MutableList<Recipe> = mutableListOf()

            // Fetch user's diet type, if balanced, treat it as null (no filter)
            var diet: String? = repository.getUserById(AppUser.instance!!.userId!!)?.dietType?.lowercase()
            if (diet == "balanced") diet = null

            // If there is still a favorite recipe that isn't processed, fetch similar recipes
            if (taken) {
                val recipes = repository.getSimilarRecipes(recipeId = recipeId).toMutableList()

                // Filter out already present recipes
                response = recipes.filterNot { currentRecipesIDs.contains(it.id) }.toMutableList()
                // Update current recipes to prevent duplicates
                currentRecipesIDs.addAll(response.map { it.id })


                response.forEach { recipe ->
                    val detailedRecipe: ExtraDetailsResponse = repository.getRecipeInfo(recipe.id)
                    val chatBotServiceViewModel = ChatBotServiceViewModel(repository)
                    if(detailedRecipe.image != null)
                        recipe.image = detailedRecipe.image
                    recipe.summary = chatBotServiceViewModel.summarizeSummary(detailedRecipe.summary)
                }

                response = response.filterNot { it.image == null }.toMutableList()

            } else {
                response = repository.getRandomRecipes(diet = diet).toMutableList()
                response.forEach { recipe ->
                    val chatBotServiceViewModel = ChatBotServiceViewModel(repository)
                    recipe.summary = chatBotServiceViewModel.summarizeSummary(recipe.summary)
                }
            }

            sharedPreferences.saveTakenIDs(takenIDs)
            sharedPreferences.saveCurrentRecipesIDs(currentRecipesIDs)


            LoadResult.Page(
                data = response,
                prevKey = if (page == 1) null else page - 1, // Handle first page
                nextKey = if (response.isEmpty()) null else page + 1 // No more data if response is empty
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
