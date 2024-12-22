package com.example.recipeapp.sharedPreferences

import android.content.Context
import android.content.SharedPreferences
import com.example.recipeapp.AppUser

class SharedPreferences(context: Context) {

    private val userId = AppUser.instance!!.userId
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPreferences_$userId", Context.MODE_PRIVATE)

    fun saveTakenIDs(takenIDs: Set<Int>) {
        sharedPreferences.edit().putStringSet("taken_ids", takenIDs.map { it.toString() }.toSet()).apply()
    }

    fun getTakenIDs(): Set<Int> {
        return sharedPreferences.getStringSet("taken_ids", emptySet())?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    fun saveCookingTipsHistory(tipsHistory: String) {
        sharedPreferences.edit().putString("cooking_tips_history", tipsHistory).apply()
    }

    fun getCookingTipsHistory(): String? {
        return sharedPreferences.getString("cooking_tips_history", null)
    }

    fun clearCookingTipsHistory() {
        sharedPreferences.edit().remove("cooking_tips_history").apply()
    }

}
