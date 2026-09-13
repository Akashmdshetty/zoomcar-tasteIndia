package com.example.tasteindia

import com.example.tasteindia.data.repository.MealFilterEngine
import com.example.tasteindia.domain.model.FilterState
import com.example.tasteindia.domain.model.Meal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FilterIntersectionTest {

    private val indianMealA = Meal("A", "Butter Chicken", "thumbA")
    private val indianMealB = Meal("B", "Dal Makhani", "thumbB")
    private val indianMealC = Meal("C", "Aloo Gobi", "thumbC")

    private val indianMeals = listOf(indianMealA, indianMealB, indianMealC)

    @Test
    fun indianFilterIntersection_enforcesIndianBoundaryStrictly() {
        // Given Indian IDs: A, B, C
        // Category IDs: B, C, D (where D is non-Indian)
        val categoryMealIds = setOf("B", "C", "D")
        // Ingredient IDs: B, E (where E is non-Indian)
        val ingredientMealIds = setOf("B", "E")

        val filterState = FilterState(
            selectedCategory = "Vegetarian",
            selectedIngredient = "Lentils"
        )

        val result = MealFilterEngine.applyFilters(
            indianMeals = indianMeals,
            filterState = filterState,
            categoryMealIds = categoryMealIds,
            ingredientMealIds = ingredientMealIds
        )

        // Intersection of (A, B, C) ∩ (B, C, D) ∩ (B, E) must be ONLY B
        assertEquals(1, result.size)
        assertEquals("B", result[0].id)

        // Verify no non-Indian ID (D or E) appeared
        val resultIds = result.map { it.id }.toSet()
        assertTrue(!resultIds.contains("D"))
        assertTrue(!resultIds.contains("E"))
    }
}
