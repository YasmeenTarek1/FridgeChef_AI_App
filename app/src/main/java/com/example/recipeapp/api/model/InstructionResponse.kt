package com.example.recipeapp.api.model

data class InstructionResponse(
    val name: String,
    val steps: List<Step>
)
