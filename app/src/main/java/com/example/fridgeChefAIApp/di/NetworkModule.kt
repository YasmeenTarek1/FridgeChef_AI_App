package com.example.fridgeChefAIApp.di

import com.example.fridgeChefAIApp.api.service.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @SpoonacularApi
    @Provides
    @Singleton
    fun provideSpoonacularRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.spoonacular.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @GoogleCustomSearchApi
    @Provides
    @Singleton
    fun provideGoogleCustomSearchRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @SpoonacularApi
    @Provides
    @Singleton
    fun provideSpoonacularApiService(@SpoonacularApi retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @GoogleCustomSearchApi
    @Provides
    @Singleton
    fun provideGoogleCustomSearchApiService(@GoogleCustomSearchApi retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
