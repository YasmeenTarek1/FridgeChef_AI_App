package com.example.recipeapp.ui.chatBotServiceFragment

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.recipeapp.AppUser
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.DetailedRecipeResponse
import com.example.recipeapp.room_DB.model.AiRecipe
import com.google.ai.client.generativeai.GenerativeModel

class ChatBotServiceViewModel (private val repository: Repository) : ViewModel() {

    suspend fun getCrazyRecipe(ingredients: String): AiRecipe{
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = "AIzaSyBygUzMLk3xTKkiiyC407TXuxI08eWPFec"
        )

        val user = repository.getUserById(AppUser.instance!!.userId!!)
        var history = ""

        // Title (String)
        var prompt = "Create a recipe no one has heard of using: $ingredients that matches my goal: ${user!!.goal} " +
                "and follows my diet type: ${user!!.dietType}. If any information is missing, just choose the option you want. " +
                "Let's begin with the title of the recipe."
        val title = generativeModel.generateContent(prompt).text.toString()
        history += "$prompt $title"

        // Image (String - URL)
        val image = repository.getAiImageForRecipeTitle("$title professional recipe picture")
        Log.d("Generated Image URL", image)

        // Servings (Int)
        prompt = "Give me the number of servings (only the number). History: $history"
        val servingsResponse = generativeModel.generateContent(prompt)
        val servings = servingsResponse.text!!.trim().toInt()

        history += "$prompt $servings"

        // Prep Time (Int - minutes)
        prompt = "Give me the prep time (only the duration in minutes). History: $history"
        val readyInMinutesResponse = generativeModel.generateContent(prompt)
        val readyInMinutes = readyInMinutesResponse.text!!.trim().toInt()

        history += "$prompt $readyInMinutes"

        // Ingredients (List<String>)
        prompt = "Give me the ingredients in an array of Strings. History: $history"
        val ingredientsResponse = generativeModel.generateContent(prompt)
        val ingredients = parseListResponse(ingredientsResponse.text.toString())

        history += "$prompt $ingredients"

        // Steps (List<String>)
        prompt = "Give me the instructions or steps in an array of Strings. History: $history"
        val stepsResponse = generativeModel.generateContent(prompt)
        val steps = parseListResponse(stepsResponse.text.toString())

        history += "$prompt $steps"

        // Tips (List<String>)
        prompt = "Give me small tips to enhance the recipe in an array of Strings. History: $history"
        val tipsResponse = generativeModel.generateContent(prompt)
        val tips = parseListResponse(tipsResponse.text.toString())

        history += "$prompt $tips"

        // Opinion (String)
        prompt = "Give me your opinion on this recipe based on my goal: ${user.goal} and diet type: ${user.dietType}. " +
                "No bullet points, just a small paragraph evaluating the recipe. History: $history"
        val opinion = generativeModel.generateContent(prompt).text

        // Create AiRecipe object and store it in the database
        val aiRecipe = AiRecipe(
            0,
            title = title,
            image = image,
            readyInMinutes = readyInMinutes,
            servings = servings,
            ingredients = ingredients.joinToString(separator = ", "),
            steps = steps.joinToString(separator = ", "),
            createdAt = System.currentTimeMillis()
        )

        Log.d("Ai Recipe" , aiRecipe.toString())
        repository.insertAiRecipe(aiRecipe)
        return aiRecipe
    }

    fun parseListResponse(response: String): List<String> {
        return response
            .removeSurrounding("[", "]")  // Remove brackets
            .split(",")  // Split by commas
            .map { it.trim().removeSurrounding("\"") }  // Clean up spaces and quotes
    }


    suspend fun getRecipeOpinion(recipe:DetailedRecipeResponse):String {
        val generativeModel =
            GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = "AIzaSyBygUzMLk3xTKkiiyC407TXuxI08eWPFec")

        val user = repository.getUserById(AppUser.instance!!.userId!!)

        val prompt = "Give me your summarized opinion in this recipe: $recipe and mention whether it matches the user's goal: ${user!!.goal}, the user's diet type: ${user!!.dietType} and if you have any small tweak to make it more related to user info given earlier, mention it"
        val response = generativeModel.generateContent(prompt)
        return response.text.toString()
    }


    suspend fun getCookingTip():String {
        val generativeModel =
            GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = "AIzaSyBygUzMLk3xTKkiiyC407TXuxI08eWPFec")

        val prompt = "Give me a small Cooking tip that no one knows and will seem interesting and make it short and simple as far as you can and add emojis"
        val response = generativeModel.generateContent(prompt)
        return response.text.toString()
    }



}