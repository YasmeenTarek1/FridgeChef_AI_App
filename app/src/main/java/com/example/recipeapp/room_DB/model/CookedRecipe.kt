package com.example.recipeapp.room_DB.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "cooked_recipes")
data class CookedRecipe(
    @PrimaryKey
    val id: Int,
    val title: String,
    val image: String?,
    val readyInMinutes: Int? = null,
    val servings: Int? = null,
    val likes: Int? = null,
    val healthScore: Double? = null,
    val dateCooked: Long = 0,  // Store as timestamp
    val createdAt: Long
): Parcelable{
    constructor() : this(0, "","" , 0, 0,0 , 0.0, 0, 0)
}
