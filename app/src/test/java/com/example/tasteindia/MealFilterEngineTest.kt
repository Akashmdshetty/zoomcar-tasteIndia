package com.example.tasteindia

import com.example.tasteindia.data.repository.MealFilterEngine
import com.example.tasteindia.domain.model.FilterState
import com.example.tasteindia.domain.model.Meal
import com.example.tasteindia.domain.model.SortOrder
import org.junit.Assert.assertEquals
import org.junit.Test

class MealFilterEngineTest {

    private val meal1 = Meal("1", "Butter Chicken", "thumb1")
    private val meal2 = Meal("2", "Dal Makhani", "thumb2")
    private val meal3 = Meal("3", "Chicken Tikka", "thumb3")
    private val meal4 = Meal("4", "Aloo Gobi", "thumb4")

    private val indianMeals = listOf(meal1, meal2, meal3, meal4)

    @Test
    fun applyFilters_defaultState_returnsAllIndianMealsInAscendingOrder() {
        val result = MealFilterEngine.applyFilters(
            indianMeals = indianMeals,
            filterState = FilterState()
        )

        // Default sort is NAME_ASC: Aloo Gobi, Butter Chicken, Chicken Tikka, Dal Makhani
        assertEquals(4, result.size)
        assertEquals("Aloo Gobi", result[0].name)
        assertEquals("Butter Chicken", result[1].name)
        assertEquals("Chicken Tikka", result[2].name)
        assertEquals("Dal Makhani", result[3].name)
    }

    @Test
    fun applyFilters_searchQuery_filtersCaseInsensitive() {
        val filterState = FilterState(searchQuery = "chicken")
        val result = MealFilterEngine.applyFilters(
            indianMeals = indianMeals,
            filterState = filterState
        )

        assertEquals(2, result.size)
        assertEquals("Butter Chicken", result[0].name)
        assertEquals("Chicken Tikka", result[1].name)
    }

    @Test
    fun applyFilters_categoryAndIngredientIntersection_enforcesIndianBoundary() {
        // Suppose category filter returns meals 1, 2, 99 (99 is non-Indian)
        val categoryMealIds = setOf("1", "2", "99")
        // Suppose ingredient filter returns meals 1, 3
        val ingredientMealIds = setOf("1", "3")

        val filterState = FilterState(
            selectedCategory = "Chicken",
            selectedIngredient = "Garlic"
        )

        val result = MealFilterEngine.applyFilters(
            indianMeals = indianMeals,
            filterState = filterState,
            categoryMealIds = categoryMealIds,
            ingredientMealIds = ingredientMealIds
        )

        // Intersection of indianMeals (1,2,3,4) ∩ (1,2,99) ∩ (1,3) = {1}
        assertEquals(1, result.size)
        assertEquals("1", result[0].id)
        assertEquals("Butter Chicken", result[0].name)
    }

    @Test
    fun applyFilters_favouritesOnly_filtersByFavIds() {
        val filterState = FilterState(favouritesOnly = true)
        val result = MealFilterEngine.applyFilters(
            indianMeals = indianMeals,
            filterState = filterState,
            favouriteIds = setOf("2", "4")
        )

        assertEquals(2, result.size)
        assertEquals("Aloo Gobi", result[0].name)
        assertEquals("Dal Makhani", result[1].name)
    }

    @Test
    fun applyFilters_sortOrder_descending_sortsZtoA() {
        val filterState = FilterState(sortOrder = SortOrder.NAME_DESC)
        val result = MealFilterEngine.applyFilters(
            indianMeals = indianMeals,
            filterState = filterState
        )

        assertEquals(4, result.size)
        assertEquals("Dal Makhani", result[0].name)
        assertEquals("Chicken Tikka", result[1].name)
        assertEquals("Butter Chicken", result[2].name)
        assertEquals("Aloo Gobi", result[3].name)
    }
}
