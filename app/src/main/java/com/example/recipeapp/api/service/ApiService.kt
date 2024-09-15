package com.example.recipeapp.api.service

import com.example.recipeapp.api.model.DetailedRecipeResponse
import com.example.recipeapp.api.model.IngredientAutocompleteResponse
import com.example.recipeapp.api.model.InstructionResponse
import com.example.recipeapp.api.model.Recipe
import com.example.recipeapp.api.model.RecipeResponse
import com.example.recipeapp.api.model.RecipeResponse2
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    companion object{
        const val API_KEY = "488151604a9240598a2fa7c7189d2c0f"
    }

    @GET("recipes/{id}/similar")
    suspend fun getSimilarRecipes(
        @Path("id") recipeId: Int,
        @Query("apiKey") apiKey: String = API_KEY,
        @Query("number") numberOfRecipes: Int = 5
    ): List<Recipe>

    @GET("recipes/random")
    suspend fun getRandomRecipes(
        @Query("apiKey") apiKey: String = API_KEY,
        @Query("number") numberOfRecipes: Int = 5,
        @Query("include-tags") diet: String? = null
    ): RecipeResponse

    @GET("recipes/{id}/information")
    suspend fun getRecipeInfo(
        @Path("id") recipeId: Int,
        @Query("apiKey") apiKey: String = API_KEY,
    ): DetailedRecipeResponse

    @GET("recipes/{id}/analyzedInstructions") // take recipe id, give me instructions
    suspend fun getInstructions(
        @Path("id") recipeId: Int,
        @Query("apiKey") apiKey: String = API_KEY,
        @Query("stepBreakdown") stepBreakdown: Boolean = true // Whether to break down the recipe steps even more.
    ): List<InstructionResponse>

    @GET("recipes/findByIngredients")
    suspend fun searchRecipesByIngredients(
        @Query("apiKey") apiKey: String = API_KEY,
        @Query("ingredients") ingredients: List<String>,       // A comma-separated list of ingredients
        @Query("number") number: Int = 5,                        //The maximum number of recipes
        @Query("ranking") ranking: Int = 2,                     // to maximize used ingredients (1) or minimize missing ingredients (2) first.
        @Query("ignorePantry") ignorePantry: Boolean = false
    ): List<Recipe>

    @GET("recipes/complexSearch")
    suspend fun searchRecipesByName(
        @Query("apiKey") apiKey: String = API_KEY,
        @Query("query") query: String,
        @Query("number") number: Int = 5
    ) : RecipeResponse2

    @GET("recipes/findByNutrients")
    suspend fun searchRecipesByNutrients(
        @Query("apiKey") apiKey: String = API_KEY,
        @Query("number") number: Int = 5,
        @Query("minCarbs") minCarbs: Int? = null,
        @Query("maxCarbs") maxCarbs: Int? = null,
        @Query("minProtein") minProtein: Int? = null,
        @Query("maxProtein") maxProtein: Int? = null,
        @Query("minFat") minFat: Int? = null,
        @Query("maxFat") maxFat: Int? = null,
        @Query("minCalories") minCalories: Int? = null,
        @Query("maxCalories") maxCalories: Int? = null,
        @Query("minSugar") minSugar: Int? = null,
        @Query("maxSugar") maxSugar: Int? = null,
        @Query("random") random: Boolean = true
    ) : List<Recipe>




    // Still want to use
    @GET("food/ingredients/search")
    suspend fun autocompleteIngredientSearch(
        @Query("apiKey") apiKey: String = API_KEY,
        @Query("query") query: String,
        @Query("number") number: Int = 5,
        @Query("sort") sort: String = "calories",
        @Query("sortDirection") sortDirection: String = "asc"
    ): IngredientAutocompleteResponse

    @GET("recipes/autocomplete")
    suspend fun autocompleteRecipeSearch(
        @Query("apiKey") apiKey: String = API_KEY,
        @Query("query") query: String,
        @Query("number") number: Int = 5
    ): RecipeResponse
}