package com.example.recipeapp.api.model



data class Instruction(
    val number: Int,
    val step: String,
    val ingredients: List<IngredientDetail>,
    val equipment: List<EquipmentDetail>
)