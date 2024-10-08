package com.example.recipeapp.ui.chatBotServiceFragment

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.recipeapp.AppUser
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.DetailedRecipeResponse
import com.example.recipeapp.room_DB.model.AiRecipe
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChatBotServiceViewModel (private val repository: Repository) : ViewModel() {

    suspend fun getCrazyRecipe(ingredients: String): AiRecipe{
        return withContext(Dispatchers.IO) {
            val generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = "AIzaSyBygUzMLk3xTKkiiyC407TXuxI08eWPFec"
            )

            val user = repository.getUserById(AppUser.instance!!.userId!!)
            var history = ""

            // Title (String)
            var prompt =
                "Create a recipe no one has heard of using: $ingredients that matches my goal: ${user!!.goal} " +
                        "and follows my diet type: ${user!!.dietType}. If any information is missing, just choose the option you want. " +
                        "Let's begin with the title of the recipe. Don't use any font styles"
            val title = generativeModel.generateContent(prompt).text.toString()
            history += "$prompt $title"

            // Image (String - URL)
            val image = repository.getAiImageForRecipeTitle("$title professional recipe picture")

            // Servings (Int)
            prompt = "Give me the number of servings (only the number) of this recipe:$history"
            val servingsResponse = generativeModel.generateContent(prompt).text!!.trim().toInt()

            // Prep Time (Int - minutes)
            prompt = "Give me the prep time (only the duration in minutes) of this recipe:$history"
            val readyInMinutesResponse =
                generativeModel.generateContent(prompt).text!!.trim().toInt()

            // Ingredients (String separated by commas)
            prompt =
                "Give me the ingredients of this recipe:$history in the form of Strings in \"\" separated by commas not in programming language"
            val ingredientsResponse = generativeModel.generateContent(prompt).text
            Log.d("Ingredients Tracing", ingredientsResponse.toString())

            history += "ingredients used: $ingredientsResponse"

            // Steps (String separated by commas)
            prompt =
                "Give me your way in detailed steps to do this recipe using the ingredients mentioned here: $history in the form of Strings in \"\" separated by commas not in programming language. Start immediately with the steps."
            val stepsResponse = generativeModel.generateContent(prompt).text
            Log.d("Steps Tracing", stepsResponse.toString())

            val aiRecipe = AiRecipe(
                0,
                title = title,
                image = image,
                readyInMinutes = readyInMinutesResponse,
                servings = servingsResponse,
                ingredients = ingredientsResponse!!,
                steps = stepsResponse!!,
                createdAt = System.currentTimeMillis()
            )

            repository.insertAiRecipe(aiRecipe)
            aiRecipe
        }
    }

    suspend fun getRecipeOpinion(recipe:DetailedRecipeResponse):String {
        return withContext(Dispatchers.IO) {
            val generativeModel =
                GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = "AIzaSyBygUzMLk3xTKkiiyC407TXuxI08eWPFec"
                )

            val user = repository.getUserById(AppUser.instance!!.userId!!)

            val prompt =
                "Give me your summarized opinion in this recipe: $recipe and mention whether it matches the user's goal: ${user!!.goal}, the user's diet type: ${user!!.dietType} and if you have any small tweak to make it more related to user info given earlier, mention it"
            val response = generativeModel.generateContent(prompt)
            response.text.toString()
        }
    }


    suspend fun getCookingTip():String {
        return withContext(Dispatchers.IO) {
            val generativeModel =
                GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = "AIzaSyBygUzMLk3xTKkiiyC407TXuxI08eWPFec"
                )

            val prompt =
                "Give me a small Cooking tip that no one knows and will seem interesting and make it short and simple as far as you can and add emojis"
            val response = generativeModel.generateContent(prompt)
            response.text.toString()
        }
    }

}