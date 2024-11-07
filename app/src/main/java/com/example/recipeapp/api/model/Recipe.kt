package com.example.recipeapp.api.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    val id: Int,
    val title: String,
    val readyInMinutes: Int,
    val servings: Int,
    var image: String,                                               // retrieved when getting similar(using ExtraDetailsResponse) or random recipes & Gemini
//
//    val missedIngredients: List<Int>? = null,       // will be available in case search by ingredients
//    var usedIngredients: @RawValue List<Ingredient>? = null,         // will be available in case search by ingredients
//
//    var ingredients: @RawValue List<Ingredient>? = null,             // Ai Recipe --> Stored ,otherwise retrieved in recipeDetailsFragment using recipe ID and stored
//    val steps: @RawValue List<Step>? = null,                         // Ai Recipe --> Stored ,otherwise retrieved in recipeStepsFragment using recipe ID and stored
//
    var summary: String                                             // retrieved when getting similar(using ExtraDetailsResponse) or random recipes & Gemini
): Parcelable{
    constructor() : this(0, "" ,0 , 0 , "" ,"")
}



//@RawValue --> allows the Parcelize plugin to serialize and deserialize
//              the complex types like List<Ingredient> using a generic method
//              (writeValue()/readValue()), which is slower but works for unsupported types.