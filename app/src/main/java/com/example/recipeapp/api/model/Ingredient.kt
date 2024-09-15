package com.example.recipeapp.api.model

data class Ingredient(
    val aisle: String,
    val amount: Double,
    val id: Int?,
    val image: String?,
    val meta: List<String>,
    val name: String,
    val original: String,
    val originalName: String,
    val unit: String,
    val unitLong: String,
    val unitShort: String
)