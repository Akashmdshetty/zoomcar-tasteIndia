package com.example.tasteindia.domain.model

/**
 * Domain model representing an Indian recipe/meal summary.
 * Decoupled from API DTO field names.
 */
data class Meal(
    val id: String,
    val name: String,
    val thumbnailUrl: String
)
