package com.example.tasteindia.data.repository

import com.example.tasteindia.data.remote.dto.MealSummaryDto
import com.example.tasteindia.domain.model.Meal

/**
 * Extension mapper converting MealSummaryDto to domain Meal model.
 * Returns null if required identifier or name fields are missing/blank.
 */
fun MealSummaryDto.toDomainModel(): Meal? {
    val cleanId = idMeal?.trim()
    val cleanName = strMeal?.trim()
    val cleanThumb = strMealThumb?.trim().orEmpty()

    if (cleanId.isNullOrBlank() || cleanName.isNullOrBlank()) {
        return null
    }

    return Meal(
        id = cleanId,
        name = cleanName,
        thumbnailUrl = cleanThumb
    )
}
