package com.example.recipeapp.api.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    val id: Int,
    val title: String,
    var readyInMinutes: Int,
    var servings: Int,
    var image: String?,                                       // retrieved when getting similar(using ExtraDetailsResponse) or random recipes & Gemini
    var summary: String,                                      // retrieved when getting similar(using ExtraDetailsResponse) or random recipes & Gemini
    var wellWrittenSummary: Int = 0
//    var ingredients: List<Ingredient>? = null,             // Ai Recipe --> retrieved from gemini & google search api for images, otherwise --> retrieved in recipeDetailsFragment using spoonacular api
//    val steps: List<Step>? = null,                         // Ai Recipe --> retrieved from gemini, otherwise --> retrieved in recipeStepsFragment using spoonacular api

): Parcelable{
    constructor() : this(0, "" ,0 , 0 , "" ,"")
}


//@RawValue --> allows the Parcelize plugin to serialize and deserialize
//              the complex types like List<Ingredient> using a generic method
//              (writeValue()/readValue()), which is slower but works for unsupported types.