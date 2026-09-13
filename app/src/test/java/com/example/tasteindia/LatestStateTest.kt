package com.example.tasteindia

import com.example.tasteindia.data.repository.MealFilterEngine
import com.example.tasteindia.domain.model.FilterState
import com.example.tasteindia.domain.model.Meal
import org.junit.Assert.assertEquals
import org.junit.Test

class LatestStateTest {

    private val meal1 = Meal("1", "Paneer Butter Masala", "thumb1")
    private val meal2 = Meal("2", "Paneer Tikka", "thumb2")
    private val meal3 = Meal("3", "Palak Paneer", "thumb3")
    private val meal4 = Meal("4", "Chicken Biryani", "thumb4")

    private val indianMeals = listOf(meal1, meal2, meal3, meal4)

    @Test
    fun latestFilterState_alwaysDeterminesFilteredOutput() {
        // Rapid sequential filter state changes
        val state1 = FilterState(searchQuery = "Paneer")
        val state2 = FilterState(searchQuery = "Chicken")
        val state3 = FilterState(searchQuery = "Tikka")

        val result1 = MealFilterEngine.applyFilters(indianMeals, state1)
        val result2 = MealFilterEngine.applyFilters(indianMeals, state2)
        val result3 = MealFilterEngine.applyFilters(indianMeals, state3)

        // Output for state1 should have 3 paneer items
        assertEquals(3, result1.size)

        // Output for state2 should have 1 chicken item
        assertEquals(1, result2.size)
        assertEquals("Chicken Biryani", result2[0].name)

        // Latest state (state3) output should have 1 tikka item
        assertEquals(1, result3.size)
        assertEquals("Paneer Tikka", result3[0].name)
    }
}
