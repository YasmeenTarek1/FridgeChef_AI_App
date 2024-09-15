package com.example.recipeapp.ui.chatBotFragment

import androidx.lifecycle.ViewModel
import com.example.recipeapp.AppUser
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.DetailedRecipeResponse
import com.google.ai.client.generativeai.GenerativeModel

class ChatBotViewModel (private val repository: Repository) : ViewModel() {


    suspend fun getCrazyRecipe(ingredients: String):String {
        val generativeModel =
            GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = "AIzaSyBygUzMLk3xTKkiiyC407TXuxI08eWPFec")

        val user = repository.getUserById(AppUser.instance!!.userId!!)

        val prompt = "Create a recipe no one has heard of using: $ingredients that matches the user's goal: ${user!!.goal} and follows also the user's diet type: ${user!!.dietType} and don't ask further questions, if any information is missing, just choose the option that you want and give me a recipe"
        val response = generativeModel.generateContent(prompt)
        return response.text.toString()
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