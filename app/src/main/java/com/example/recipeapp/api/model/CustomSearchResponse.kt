package com.example.recipeapp.api.model

data class CustomSearchResponse(
    val items: List<SearchResult>?
)

data class SearchResult(
    val link: String
)