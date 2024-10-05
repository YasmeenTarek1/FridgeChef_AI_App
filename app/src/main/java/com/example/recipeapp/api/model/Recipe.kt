package com.example.recipeapp.api.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Recipe(
    val id: Int,
    val title: String,
    var image: String?,
    val readyInMinutes: Int? = null,
    val servings: Int? = null,
    var likes: Int? = null,
    val missedIngredients: @RawValue List<Ingredient>? = null,
    val usedIngredients: @RawValue List<Ingredient>? = null,
    var healthScore: Double? = null,
    val ingredients: String? = null,  // Stored as a comma-separated string
    val steps: String? = null         // Stored as a comma-separated string
): Parcelable



//@RawValue --> allows the Parcelize plugin to serialize and deserialize
//              the complex types like List<Ingredient> using a generic method
//              (writeValue()/readValue()), which is slower but works for unsupported types.