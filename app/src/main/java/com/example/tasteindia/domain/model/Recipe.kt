package com.example.tasteindia.domain.model

/**
 * Domain model representing a Recipe/Meal.
 * Decoupled from API DTOs and database entities.
 */
data class Recipe(
    val id: String,
    val name: String,
    val category: String,
    val area: String,
    val instructions: String,
    val thumbnailUrl: String,
    val isFavourite: Boolean = false
)
