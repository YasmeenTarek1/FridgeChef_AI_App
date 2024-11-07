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
    val image: String,
    val readyInMinutes: Int,
    val servings: Int,
    val createdAt: Long,
    var summary: String
): Parcelable {
    constructor() : this(0, "", "",0, 0 , 0 , "")
}
