import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services") // Firebase Google Services plugin
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
}

// Load properties from local.properties
val localProperties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

// Load the API keys from local.properties
val apiKeySpoonacular: String = localProperties.getProperty("API_KEY_spoonacular")
val apiKeyGemini: String = localProperties.getProperty("API_KEY_gemini")
val apiKeyCustomSearch: String = localProperties.getProperty("API_KEY_custom_search")

android {
    namespace = "com.example.fridgeChefAIApp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.fridgeChefAIApp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "API_KEY_Spoonacular", "\"$apiKeySpoonacular\"")
        buildConfigField("String", "API_KEY_Gemini", "\"$apiKeyGemini\"")
        buildConfigField("String", "API_KEY_Custom_Search", "\"$apiKeyCustomSearch\"")
    }

    buildTypes {
        debug {
            isDebuggable = true
        }
        release {
            isDebuggable = false
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        dataBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    // Fragment library
    implementation(libs.androidx.fragment.ktx)

    // Navigation for Fragments
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Firebase Auth
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Google Sign-In
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.androidx.credentials)

    // Facebook Sign-In
    implementation(libs.facebook.login)
    implementation(libs.facebook.android.sdk)

    // Retrofit + GSON
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    annotationProcessor(libs.androidx.room.compiler)

    // Firestore
    implementation(libs.firebase.firestore.ktx)

    // Logging Interceptors
    implementation(libs.logging.interceptor)

    // Paging library
    implementation(libs.androidx.paging.common.android)
    implementation(libs.androidx.paging.runtime)

    // Glide Library
    implementation(libs.glide)
    ksp(libs.compiler)
    annotationProcessor(libs.compiler)
    ksp(libs.ksp)

    // Gemini
    implementation(libs.generativeai)

    // Markdown Support
    implementation(libs.core)

    // Firebase Storage
    implementation(libs.firebase.storage)

    // Swipe Reveal Layout
    implementation(libs.swipereveallayout)

    // LottieFiles
    implementation(libs.lottie)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}