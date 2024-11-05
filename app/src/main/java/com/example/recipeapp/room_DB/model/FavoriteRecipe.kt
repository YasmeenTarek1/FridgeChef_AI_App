package com.example.recipeapp.room_DB.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "favorite_recipes")
data class FavoriteRecipe(
    @PrimaryKey
    val id: Int,
    val title: String,
    val image: String?,
    val readyInMinutes: Int? = null,
    val servings: Int? = null,
    val createdAt: Long,
    val ingredients: String? = null,  // Stored as a comma-separated string
    val steps: String? = null,        // Stored as a comma-separated string
    var summary: String? = null
): Parcelable {
    constructor() : this(0, "", "",0, 0 , 0)
}
