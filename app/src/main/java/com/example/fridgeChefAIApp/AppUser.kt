package com.example.fridgeChefAIApp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
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