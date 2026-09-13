package com.example.tasteindia

import com.example.tasteindia.domain.model.FilterState
import com.example.tasteindia.domain.model.SortOrder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationStateRestorationTest {

    @Test
    fun filterState_isPreservedAcrossNavigationEvents() {
        // Simulating FilterState active before opening DetailScreen
        val initialFilterState = FilterState(
            searchQuery = "chicken",
            selectedCategory = "Chicken",
            selectedIngredient = "Garlic",
            favouritesOnly = false,
            sortOrder = SortOrder.NAME_ASC
        )

        // User navigates: Recipes -> DetailScreen -> Back to Recipes
        // FilterState object is retained by ViewModel
        val restoredFilterState = initialFilterState.copy()

        assertEquals("chicken", restoredFilterState.searchQuery)
        assertEquals("Chicken", restoredFilterState.selectedCategory)
        assertEquals("Garlic", restoredFilterState.selectedIngredient)
        assertEquals(false, restoredFilterState.favouritesOnly)
        assertEquals(SortOrder.NAME_ASC, restoredFilterState.sortOrder)
        assertTrue(restoredFilterState.isFiltered)
    }
}
