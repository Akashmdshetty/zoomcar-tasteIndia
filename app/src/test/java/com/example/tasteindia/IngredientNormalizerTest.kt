package com.example.tasteindia

import com.example.tasteindia.data.remote.dto.MealDetailsDto
import com.example.tasteindia.data.repository.toNormalizedIngredients
import org.junit.Assert.assertEquals
import org.junit.Test

class IngredientNormalizerTest {

    @Test
    fun toNormalizedIngredients_trimsWhitespaceAndOmitsEmptyPairs() {
        val dto = MealDetailsDto(
            strIngredient1 = "Chicken ",
            strMeasure1 = " 500g ",
            strIngredient2 = " Yogurt ",
            strMeasure2 = "200ml",
            strIngredient3 = "",
            strMeasure3 = "",
            strIngredient4 = null,
            strMeasure4 = null
        )

        val normalized = dto.toNormalizedIngredients()

        assertEquals(2, normalized.size)
        assertEquals("Chicken", normalized[0].name)
        assertEquals("500g", normalized[0].measure)
        assertEquals("Yogurt", normalized[1].name)
        assertEquals("200ml", normalized[1].measure)
    }

    @Test
    fun toNormalizedIngredients_allEmpty_returnsEmptyList() {
        val dto = MealDetailsDto(
            strIngredient1 = "   ",
            strMeasure1 = "   "
        )

        val normalized = dto.toNormalizedIngredients()

        assertEquals(0, normalized.size)
    }
}
