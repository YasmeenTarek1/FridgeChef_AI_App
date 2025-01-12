package com.example.fridgeChefAIApp.room_DB.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "to_buy_ingredients")
data class ToBuyIngredient(
    @PrimaryKey
    val name: String,
    val image: String?,
    val createdAt: Long
): Parcelable {
    constructor() : this("", null,  0)
}

