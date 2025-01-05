package com.example.recipeapp.api.model

data class SearchByNameResponse(
    val results: List<Recipe>
)

data class RandomRecipeResponse(
    val recipes: List<Recipe>
)
