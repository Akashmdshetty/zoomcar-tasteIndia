package com.example.tasteindia.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for API network responses.
 * Uses Kotlinx Serialization annotations.
 */
@Serializable
data class RecipeDto(
    @SerialName("idMeal") val idMeal: String? = null,
    @SerialName("strMeal") val strMeal: String? = null,
    @SerialName("strCategory") val strCategory: String? = null,
    @SerialName("strArea") val strArea: String? = null,
    @SerialName("strInstructions") val strInstructions: String? = null,
    @SerialName("strMealThumb") val strMealThumb: String? = null
)
