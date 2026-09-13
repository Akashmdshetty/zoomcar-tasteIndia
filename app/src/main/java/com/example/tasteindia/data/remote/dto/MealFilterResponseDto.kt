package com.example.tasteindia.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response DTO for filter.php?a=Indian endpoint.
 */
@Serializable
data class MealFilterResponseDto(
    @SerialName("meals") val meals: List<MealSummaryDto>? = null
)

/**
 * Summary DTO for an individual meal in the filter response.
 */
@Serializable
data class MealSummaryDto(
    @SerialName("idMeal") val idMeal: String? = null,
    @SerialName("strMeal") val strMeal: String? = null,
    @SerialName("strMealThumb") val strMealThumb: String? = null
)
