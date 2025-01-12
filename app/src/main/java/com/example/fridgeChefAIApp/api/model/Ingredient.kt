package com.example.fridgeChefAIApp.api.model

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

