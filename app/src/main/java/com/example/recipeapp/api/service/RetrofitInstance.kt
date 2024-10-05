package com.example.recipeapp.api.service

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance { // is used to make Network Calls

    // httpClient is just to enable logging of network requests and responses using the HttpLoggingInterceptor
    private val httpClient: OkHttpClient by lazy {
        val httpLoggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build()
    }

    private fun getRetrofitInstance(BASE_URL:String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .client(httpClient)
            .build()
    }

    fun getApiService(BASE_URL:String): ApiService {
        return getRetrofitInstance(BASE_URL).create(ApiService::class.java)
    }
}