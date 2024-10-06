package com.example.recipeapp.room_DB.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "user_info")
data class UserInfo(
    @PrimaryKey
    val id: String,
    var name: String,
    var height: Int,
    var weight: Int,
    var dietType: String,         // e.g., "Vegan", "Keto", "Low Carb" , "Balanced"
    var goal: String,             // e.g., "Gain Muscle", "Lose Fat", "Stay Fit"
    var age: Int,
    val gender: String,
    var bmi: Double,
    var image: String? = null
): Parcelable{
    constructor() : this("", "",0, 0, "", "", 0, "", 0.0 , null)
}
