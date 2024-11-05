package com.example.recipeapp.api.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Recipe(
    val id: Int,
    val title: String,
    var image: String? = null,
    val readyInMinutes: Int? = null,
    val servings: Int? = null,
    val missedIngredients: @RawValue List<Ingredient>? = null,
    var usedIngredients: @RawValue List<Ingredient>? = null,
    val ingredients: String? = null,  // Stored as a comma-separated string
    val steps: String? = null,        // Stored as a comma-separated string
    var summary: String? = null
): Parcelable{
    constructor() : this(0, "")
}



//@RawValue --> allows the Parcelize plugin to serialize and deserialize
//              the complex types like List<Ingredient> using a generic method
//              (writeValue()/readValue()), which is slower but works for unsupported types.