package com.example.tasteindia.domain.model

/**
 * Domain model representing complete recipe details.
 */
data class MealDetails(
    val id: String,
    val name: String,
    val category: String,
    val area: String,
    val instructions: String,
    val thumbnailUrl: String,
    val tags: List<String>,
    val youtubeUrl: String?,
    val sourceUrl: String?,
    val ingredients: List<Ingredient>
)
