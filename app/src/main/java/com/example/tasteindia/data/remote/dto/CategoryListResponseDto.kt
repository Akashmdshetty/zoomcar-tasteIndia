package com.example.tasteindia.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryListResponseDto(
    @SerialName("meals") val meals: List<CategorySummaryDto>? = null
)

@Serializable
data class CategorySummaryDto(
    @SerialName("strCategory") val strCategory: String? = null
)

@Serializable
data class IngredientListResponseDto(
    @SerialName("meals") val meals: List<IngredientSummaryDto>? = null
)

@Serializable
data class IngredientSummaryDto(
    @SerialName("strIngredient") val strIngredient: String? = null
)
