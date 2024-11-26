package com.example.recipeapp.api.service

import com.example.recipeapp.BuildConfig
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

    companion object{
        const val API_KEY_SPOONACULAR = BuildConfig.API_KEY_Spoonacular
        const val API_KEY_CUSTOM_SEARCH = BuildConfig.API_KEY_Custom_Search
    }

    @GET("recipes/{id}/similar")
    suspend fun getSimilarRecipes(
        @Path("id") recipeId: Int,
        @Query("apiKey") apiKey: String = API_KEY_SPOONACULAR,
        @Query("number") numberOfRecipes: Int = 5
    ): List<Recipe>

    @GET("recipes/random")
    suspend fun getRandomRecipes(
        @Query("apiKey") apiKey: String = API_KEY_SPOONACULAR,
        @Query("number") numberOfRecipes: Int = 5,
        @Query("include-tags") diet: String? = null
    ): RandomRecipeResponse

    @GET("recipes/{id}/information") // take recipe id , give me the recipe image
    suspend fun getRecipeInfo(
        @Path("id") recipeId: Int,
        @Query("apiKey") apiKey: String = API_KEY_SPOONACULAR,
    ): ExtraDetailsResponse

    @GET("recipes/{id}/analyzedInstructions") // take recipe id, give me instructions
    suspend fun getInstructions(
        @Path("id") recipeId: Int,
        @Query("apiKey") apiKey: String = API_KEY_SPOONACULAR,
        @Query("stepBreakdown") stepBreakdown: Boolean = true // Whether to break down the recipe steps even more.
    ): List<InstructionResponse>

    @GET("recipes/{id}/ingredientWidget.json")
    suspend fun getIngredients(
        @Path("id") recipeId: Int,
        @Query("apiKey") apiKey: String = API_KEY_SPOONACULAR
    ): IngredientResponse2

    @GET("recipes/findByIngredients")
    suspend fun searchRecipesByIngredients(
        @Query("apiKey") apiKey: String = API_KEY_SPOONACULAR,
        @Query("ingredients") ingredients: List<String>,       // A comma-separated list of ingredients
        @Query("number") number: Int = 15,                        //The maximum number of recipes
        @Query("ranking") ranking: Int = 2,                     // to maximize used ingredients (1) or minimize missing ingredients (2) first.
        @Query("ignorePantry") ignorePantry: Boolean = false
    ): List<Recipe>

    @GET("recipes/complexSearch")
    suspend fun searchRecipesByName(
        @Query("apiKey") apiKey: String = API_KEY_SPOONACULAR,
        @Query("query") query: String,
        @Query("number") number: Int = 20
    ) : SearchByNameResponse

    @GET("recipes/findByNutrients")
    suspend fun searchRecipesByNutrients(
        @Query("apiKey") apiKey: String = API_KEY_SPOONACULAR,
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
        @Query("apiKey") apiKey: String = API_KEY_SPOONACULAR,
        @Query("query") query: String,
        @Query("number") number: Int = 5,
    ): IngredientAutocompleteResponse


    @GET("recipes/autocomplete")
    suspend fun autocompleteRecipeSearch(
        @Query("apiKey") apiKey: String = API_KEY_SPOONACULAR,
        @Query("query") query: String,
        @Query("number") number: Int = 5
    ): List<Recipe>

    @GET("customsearch/v1")
    suspend fun getRecipeOrIngredientImage(
        @Query("key") apiKey: String = API_KEY_CUSTOM_SEARCH,
        @Query("q") title: String,
        @Query("num") number: Int = 1,
        @Query("cx") cx: String = "d7935696c02424683",
        @Query("searchType") searchType: String = "image"
    ): CustomSearchResponse

}