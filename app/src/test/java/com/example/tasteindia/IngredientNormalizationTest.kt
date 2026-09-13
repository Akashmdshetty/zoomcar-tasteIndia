package com.example.tasteindia

import com.example.tasteindia.data.remote.dto.MealDetailsDto
import com.example.tasteindia.data.repository.toDomainModel
import com.example.tasteindia.data.repository.toNormalizedIngredients
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class IngredientNormalizationTest {

    @Test
    fun toNormalizedIngredients_convertsValidPairs_andOmitsBlankOrNullEntries() {
        val dto = MealDetailsDto(
            idMeal = "52850",
            strMeal = "Butter Chicken",
            strCategory = "Chicken",
            strArea = "Indian",
            strInstructions = "Cook slowly",
            strMealThumb = "https://example.com/thumb.jpg",
            strIngredient1 = "  Chicken  ",
            strMeasure1 = " 500g ",
            strIngredient2 = "Butter",
            strMeasure2 = "2 tbsp",
            strIngredient3 = "  ", // blank ingredient -> must be omitted
            strMeasure3 = "1 cup",
            strIngredient4 = null, // null ingredient -> omitted
            strMeasure4 = null
        )

        val ingredients = dto.toNormalizedIngredients()

        assertEquals(2, ingredients.size)
        assertEquals("Chicken", ingredients[0].name)
        assertEquals("500g", ingredients[0].measure)
        assertEquals("Butter", ingredients[1].name)
        assertEquals("2 tbsp", ingredients[1].measure)
    }

    @Test
    fun mealDetailDto_toDomainModel_mapsAllFieldsCorrectly() {
        val dto = MealDetailsDto(
            idMeal = "52850",
            strMeal = "Butter Chicken",
            strCategory = "Chicken",
            strArea = "Indian",
            strInstructions = "Marinate and cook.",
            strMealThumb = "https://example.com/butter_chicken.jpg",
            strTags = "Curry,Spicy",
            strYoutube = "https://www.youtube.com/watch?v=12345",
            strSource = "https://example.com/recipe",
            strIngredient1 = "Chicken",
            strMeasure1 = "500g"
        )

        val domainDetails = dto.toDomainModel()

        assertNotNull(domainDetails)
        assertEquals("52850", domainDetails?.id)
        assertEquals("Butter Chicken", domainDetails?.name)
        assertEquals("Chicken", domainDetails?.category)
        assertEquals("Indian", domainDetails?.area)
        assertEquals("Marinate and cook.", domainDetails?.instructions)
        assertEquals(listOf("Curry", "Spicy"), domainDetails?.tags)
        assertEquals("https://www.youtube.com/watch?v=12345", domainDetails?.youtubeUrl)
        assertEquals("https://example.com/recipe", domainDetails?.sourceUrl)
        assertEquals(1, domainDetails?.ingredients?.size)
        assertEquals("Chicken", domainDetails?.ingredients?.get(0)?.name)
        assertEquals("500g", domainDetails?.ingredients?.get(0)?.measure)
    }
}
