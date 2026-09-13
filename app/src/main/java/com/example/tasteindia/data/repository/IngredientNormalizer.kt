package com.example.tasteindia.data.repository

import com.example.tasteindia.data.remote.dto.MealDetailsDto
import com.example.tasteindia.domain.model.Ingredient

/**
 * Pure normalization function extracting non-empty Ingredient pairs from raw DTO fields.
 */
fun MealDetailsDto.toNormalizedIngredients(): List<Ingredient> {
    val rawIngredients = listOf(
        strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5,
        strIngredient6, strIngredient7, strIngredient8, strIngredient9, strIngredient10,
        strIngredient11, strIngredient12, strIngredient13, strIngredient14, strIngredient15,
        strIngredient16, strIngredient17, strIngredient18, strIngredient19, strIngredient20
    )

    val rawMeasures = listOf(
        strMeasure1, strMeasure2, strMeasure3, strMeasure4, strMeasure5,
        strMeasure6, strMeasure7, strMeasure8, strMeasure9, strMeasure10,
        strMeasure11, strMeasure12, strMeasure13, strMeasure14, strMeasure15,
        strMeasure16, strMeasure17, strMeasure18, strMeasure19, strMeasure20
    )

    val result = mutableListOf<Ingredient>()

    for (i in rawIngredients.indices) {
        val name = rawIngredients[i]?.trim()
        val measure = rawMeasures.getOrNull(i)?.trim().orEmpty()

        if (!name.isNullOrBlank()) {
            result.add(
                Ingredient(
                    name = name,
                    measure = measure
                )
            )
        }
    }

    return result
}

/**
 * Maps MealDetailsDto into domain MealDetails model.
 */
fun MealDetailsDto.toDomainModel(): com.example.tasteindia.domain.model.MealDetails? {
    val cleanId = idMeal?.trim()
    val cleanName = strMeal?.trim()

    if (cleanId.isNullOrBlank() || cleanName.isNullOrBlank()) {
        return null
    }

    val parsedTags = strTags?.split(",")
        ?.map { it.trim() }
        ?.filter { it.isNotBlank() }
        .orEmpty()

    val cleanYoutube = strYoutube?.trim().takeIf { !it.isNullOrBlank() && (it.startsWith("http://") || it.startsWith("https://")) }
    val cleanSource = strSource?.trim().takeIf { !it.isNullOrBlank() && (it.startsWith("http://") || it.startsWith("https://")) }

    return com.example.tasteindia.domain.model.MealDetails(
        id = cleanId,
        name = cleanName,
        category = strCategory?.trim().orEmpty(),
        area = strArea?.trim().orEmpty(),
        instructions = strInstructions?.trim().orEmpty(),
        thumbnailUrl = strMealThumb?.trim().orEmpty(),
        tags = parsedTags,
        youtubeUrl = cleanYoutube,
        sourceUrl = cleanSource,
        ingredients = toNormalizedIngredients()
    )
}
