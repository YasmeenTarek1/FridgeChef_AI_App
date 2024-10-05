package com.example.recipeapp.room_DB.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "to_buy_ingredients")
data class ToBuyIngredient(
    @PrimaryKey
    val id: Int,
    val name: String,
    val amount: Double,
    val unit: String,
    val aisle: String,
    val image: String?,
    val createdAt: Long
): Parcelable {
    constructor() : this(0, "",0.0, "", "", "", 0)
}

