package com.example.recipeapp.api.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExtraDetailsResponse(
    val image: String?,
    val summary: String
):Parcelable

