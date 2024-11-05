
package com.example.recipeapp.room_DB.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "ai_recipes")
data class AiRecipe(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val image: String,
    val summary: String,
    val readyInMinutes: Int,
    val servings: Int,
    val ingredients: String,  // Stored as a comma-separated string
    val steps: String,        // Stored as a comma-separated string
    var createdAt: Long,
    ): Parcelable{
    constructor() : this(0, "","" , "" , 0 , 0 , "" , "", 0)
}

