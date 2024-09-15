package com.example.recipeapp.api.model

data class RecipeResponse2(
    val offset: Int,
    val number: Int,
    val results: List<Recipe>,
    val totalResults: Int
)
