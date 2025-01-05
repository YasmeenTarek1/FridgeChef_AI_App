package com.example.recipeapp.api.model

data class Ingredient(
    var image: String?,
    val name: String
)

data class IngredientResponse2(
    val ingredients: List<Ingredient>
)

data class IngredientAutocompleteResponse(
    val results: List<Ingredient>
)

