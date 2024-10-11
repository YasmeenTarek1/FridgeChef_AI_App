package com.example.recipeapp.sharedPrefrences

import android.content.Context
import android.content.SharedPreferences

class SharedPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("recipe_prefs", Context.MODE_PRIVATE)

    fun saveTakenIDs(takenIDs: Set<Int>) {
        sharedPreferences.edit().putStringSet("taken_ids", takenIDs.map { it.toString() }.toSet()).apply()
    }

    fun getTakenIDs(): Set<Int> {
        return sharedPreferences.getStringSet("taken_ids", emptySet())?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    fun saveCurrentRecipesIDs(takenIDs: Set<Int>) {
        sharedPreferences.edit().putStringSet("current_recipes_ids", takenIDs.map { it.toString() }.toSet()).apply()
    }

    fun getCurrentRecipesIDs(): Set<Int> {
        return sharedPreferences.getStringSet("current_recipes_ids", emptySet())?.map { it.toInt() }?.toSet() ?: emptySet()
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
