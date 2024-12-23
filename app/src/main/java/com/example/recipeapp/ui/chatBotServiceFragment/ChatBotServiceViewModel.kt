package com.example.recipeapp.ui.chatBotServiceFragment

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.recipeapp.AppUser
import com.example.recipeapp.BuildConfig
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.Recipe
import com.example.recipeapp.room_DB.model.AiRecipe
import com.example.recipeapp.sharedPreferences.SharedPreferences
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class ChatBotServiceViewModel (private val repository: Repository , private val sharedPreferences: SharedPreferences? = null) : ViewModel() {

    val recipes: Flow<List<AiRecipe>> = repository.getAllAiRecipes()

    companion object {
        const val API_KEY_GEMINI = BuildConfig.API_KEY_Gemini
        private const val API_CALL_DELAY_MS = 1500L // Delay of 1.5 second to respect rate limits
        private val rateLimitMutex = Mutex() // Mutex to synchronize API calls
    }

    private suspend fun safeGenerateContent(
        generativeModel: GenerativeModel,
        prompt: String
    ): String? {
        return rateLimitMutex.withLock {
            delay(API_CALL_DELAY_MS)
            try {
                val response = generativeModel.generateContent(prompt)
                response.text
            } catch (e: Exception) {
                Log.d("Gemini Error" ,"quota exceeded or other exception")
                throw e
            }
        }
    }

    suspend fun getCrazyRecipe(ingredients: String): Recipe{
        return withContext(Dispatchers.IO) {
            val generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = API_KEY_GEMINI
            )

            val user = repository.getUserById(AppUser.instance!!.userId!!)
            var history = "Response History: "

            // Title (String)
            var prompt = "Mention only the title of a recipe no one has heard of using: $ingredients that matches my goal: ${user!!.goal} " +
                         "and follows my diet type: ${user!!.dietType}. If any information is missing, just guess the option you want. Don't ask any questions. " +
                         "Don't use any font styles"
            val title = safeGenerateContent(generativeModel, prompt).toString()
            history += "$prompt $title"


            // Image (String - URL)
            val image = repository.getRecipeOrIngredientImage("$title recipe horizontal image")


            // Summary (String)
            prompt = "Give me a brief summary of this recipe: $history without mentioning its title or ingredients"
            val summaryResponse = safeGenerateContent(generativeModel, prompt)
            history += "quick summary: $summaryResponse"


            // Servings (Int)
            prompt = "Give me the number of servings (only the number) of this recipe:$history"
            val servingsResponse = safeGenerateContent(generativeModel, prompt)!!.trim().toInt()


            // Prep Time (Int - minutes)
            prompt = "Give me the prep time (only the duration in minutes) of this recipe:$history"
            val readyInMinutesResponse = safeGenerateContent(generativeModel, prompt)!!.trim().toInt()


            // Ingredients
            prompt = "Give me only the names of the ingredients of this recipe:$history in the form of Strings in \"\" separated by commas not in programming language"
            val ingredientsResponse = safeGenerateContent(generativeModel, prompt)
            history += "ingredients used: $ingredientsResponse"


            // Steps
            prompt = "Give me your way in detailed steps to do this recipe using the ingredients mentioned here: $history in the form of Strings in \"\" separated by commas not lines and not in programming language or mark down." +
                    " Start immediately with the steps without mentioning any titles" +
                    " Every step is a complete sentence"
            val stepsResponse = safeGenerateContent(generativeModel, prompt)


            val aiRecipe = AiRecipe(
                0,
                title = title,
                image = image,
                readyInMinutes = readyInMinutesResponse,
                servings = servingsResponse,
                ingredients = ingredientsResponse!!,
                steps = stepsResponse!!,
                summary = summaryResponse!!,
                createdAt = System.currentTimeMillis()
            )

            repository.insertAiRecipe(aiRecipe)

            val recipe = Recipe(
                id = repository.getLastInsertedAiRecipeID(),
                title = aiRecipe.title,
                image = aiRecipe.image,
                readyInMinutes = aiRecipe.readyInMinutes,
                servings = aiRecipe.servings,
                summary = aiRecipe.summary
            )

            recipe
        }
    }

    suspend fun updateFirestore(){
        withContext(Dispatchers.IO) {
            recipes.collect { recipes ->
                repository.updateAiRecipesInFirestore(recipes)
            }
        }
    }

    suspend fun getRecipeOpinion(recipe: Recipe): String {
        return withContext(Dispatchers.IO) {
            val generativeModel =
                GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = API_KEY_GEMINI
                )

            val user = repository.getUserById(AppUser.instance!!.userId!!)

            val prompt = """
            Please give your summarized opinion on this recipe in Markdown format with some emojis. 
            
            Include:
            - Whether it matches the user's goal: ${user!!.goal} and diet type: ${user.dietType}
            - Any small tweaks to better fit the user's profile, if applicable.
            
            Recipe Details:
            $recipe
            """.trimIndent()

            val response = safeGenerateContent(generativeModel, prompt)
            response.toString()
        }
    }

    suspend fun getCookingTip():String {
        return withContext(Dispatchers.IO) {
            val user = repository.getUserById(AppUser.instance!!.userId!!)
            var cookingTipHistory: String = sharedPreferences!!.getCookingTipsHistory().toString()

            val generativeModel =
                GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = API_KEY_GEMINI
                )

            val prompt = "Give me a short unique professional cooking or health tip no one knows decorated with relevant emojis, follows my diet type: ${user!!.dietType}, excluding all tips similar to these $cookingTipHistory by choosing completely different ingredients, separate between the title and the content start the tip immediately without asking any questions"
            val response = safeGenerateContent(generativeModel, prompt)

            cookingTipHistory += ", ${response.toString()}"
            cookingTipHistory = cookingTipHistory.takeLast(3000)

            sharedPreferences.saveCookingTipsHistory(cookingTipHistory)
            response.toString()
        }
    }

    suspend fun summarizeSummary(summary: String): String {
        return withContext(Dispatchers.IO) {

            val generativeModel =
                GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = API_KEY_GEMINI
                )

            val prompt = "summarize this paragraph $summary mentioning only the description of the recipe and its nutritional values if exist"
            val response = safeGenerateContent(generativeModel, prompt)

            response.toString()
        }
    }

}