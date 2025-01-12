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
    val readyInMinutes: Int,
    val servings: Int,
    val image: String?,
    val createdAt: Long,
    var summary: String,
    var wellWrittenSummary: Int = 1
): Parcelable{
    constructor() : this(0, "" ,0, 0,null , 0 , "")
}
