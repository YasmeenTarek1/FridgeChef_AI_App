package com.example.recipeapp

import android.app.Application

class AppUser: Application() {
    var userId: String? = null

    companion object {
        var instance: AppUser? = null
            get() {
                if (field == null) {
                    field = AppUser()
                }
                return field
            }
            private set
    }
}