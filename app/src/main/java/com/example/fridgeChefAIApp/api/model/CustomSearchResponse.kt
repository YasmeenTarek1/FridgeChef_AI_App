package com.example.fridgeChefAIApp.api.model

data class CustomSearchResponse(
    val items: List<SearchResult>?
)

data class SearchResult(
    val link: String
)