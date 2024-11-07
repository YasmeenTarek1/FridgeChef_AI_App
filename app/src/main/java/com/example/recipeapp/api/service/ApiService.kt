package com.example.recipeapp.api.service

import com.example.recipeapp.api.model.CustomSearchResponse
import com.example.recipeapp.api.model.ExtraDetailsResponse
import com.example.recipeapp.api.model.IngredientAutocompleteResponse
import com.example.recipeapp.api.model.IngredientResponse2
import com.example.recipeapp.api.model.InstructionResponse
import com.example.recipeapp.api.model.RandomRecipeResponse
import com.example.recipeapp.api.model.Recipe
import com.example.recipeapp.api.model.SearchByNameResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // a547ab5aeb1d4f48a8bb7345e388cfc8
    // c5494363166246e185c7b8e837ba8c7a
    // ad28d9e80f7c42219ce7dd5c918b9cb0
    // 488151604a9240598a2fa7c7189d2c0f
    // ebb0fbc667bd4b4cb68f97ae56fc4ded
    // b6a39b82b408445f9100f6e8a7436249
    // e46d27c80c5c41948205811245e738f0
    companion object{
        const val API_KEY = "c5494363166246e185c7b8e837ba8c7a"
    }

    @GET("recipes/{id}/similar")
    suspend fun getSimilarRecipes(
        @Path("id") recipeId: Int,
        @Query("apiKey") apiKey: String = API_KEY,
        @Query("number") numberOfRecipes: Int = 4
    ): List<Recipe>

    @GET("recipes/random")
    suspend fun getRandomRecipes(
        @Query("apiKey") apiKey: String = API_KEY,
        @Query("number") numberOfRecipes: Int = 3,
        @Query("include-tags") diet: String? = null
    ): RandomRecipeResponse

    @GET("recipes/{id}/information") // take recipe id , give me the recipe image
    suspend fun getRecipeInfo(
        @Path("id") recipeId: Int,
        @Query("apiKey") apiKey: String = API_KEY,
    ): ExtraDetailsResponse

    @GET("recipes/{id}/analyzedInstructions") // take recipe id, give me instructions
    suspend fun getInstructions(
        @Path("id") recipeId: Int,
        @Query("apiKey") apiKey: String = API_KEY,
        @Query("stepBreakdown") stepBreakdown: Boolean = true // Whether to break down the recipe steps even more.
    ): List<InstructionResponse>

    @GET("recipes/{id}/ingredientWidget.json")
    suspend fun getIngredients(
        @Path("id") recipeId: Int,
        @Query("apiKey") apiKey: String = API_KEY
    ): IngredientResponse2

    @GET("recipes/findByIngredients")
    suspend fun searchRecipesByIngredients(
        @Query("apiKey") apiKey: String = API_KEY,
        @Query("ingredients") ingredients: List<String>,       // A comma-separated list of ingredients
        @Query("number") number: Int = 15,                        //The maximum number of recipes
        @Query("ranking") ranking: Int = 2,                     // to maximize used ingredients (1) or minimize missing ingredients (2) first.
        @Query("ignorePantry") ignorePantry: Boolean = false
    ): List<Recipe>

    @GET("recipes/complexSearch")
    suspend fun searchRecipesByName(
        @Query("apiKey") apiKey: String = API_KEY,
        @Query("query") query: String,
        @Query("number") number: Int = 20
    ) : SearchByNameResponse

    @GET("recipes/findByNutrients")
    suspend fun searchRecipesByNutrients(
        @Query("apiKey") apiKey: String = API_KEY,
        @Query("number") number: Int = 20,
        @Query("maxCarbs") maxCarbs: Int? = null,
        @Query("maxProtein") maxProtein: Int? = null,
        @Query("maxFat") maxFat: Int? = null,
        @Query("maxCalories") maxCalories: Int? = null,
        @Query("maxSugar") maxSugar: Int? = null,
        @Query("random") random: Boolean = true
    ) : List<Recipe>

    @GET("food/ingredients/search")
    suspend fun autocompleteIngredientSearch(
        @Query("apiKey") apiKey: String = API_KEY,
        @Query("query") query: String,
        @Query("number") number: Int = 5,
    ): IngredientAutocompleteResponse


    @GET("recipes/autocomplete")
    suspend fun autocompleteRecipeSearch(
        @Query("apiKey") apiKey: String = API_KEY,
        @Query("query") query: String,
        @Query("number") number: Int = 5
    ): List<Recipe>

    @GET("customsearch/v1")
    suspend fun getAiImageForRecipeTitle(
        @Query("key") apiKey: String = "AIzaSyBXAW4Ybjb3iLqiOgC7-irQijjSczuWs90",
        @Query("q") title: String,
        @Query("num") number: Int = 1,
        @Query("cx") cx: String = "d7935696c02424683",
        @Query("searchType") searchType: String = "image"
    ): CustomSearchResponse
}