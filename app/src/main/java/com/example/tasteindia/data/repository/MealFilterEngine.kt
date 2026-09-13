package com.example.tasteindia.data.repository

import com.example.tasteindia.domain.model.FilterState
import com.example.tasteindia.domain.model.Meal
import com.example.tasteindia.domain.model.SortOrder

/**
 * Pure filtering engine responsible for enforcing the Indian boundary invariant,
 * category/ingredient set intersections, local search, favourites filtering, and sorting.
 */
object MealFilterEngine {

    fun applyFilters(
        indianMeals: List<Meal>,
        filterState: FilterState,
        categoryMealIds: Set<String>? = null,
        ingredientMealIds: Set<String>? = null,
        favouriteIds: Set<String> = emptySet()
    ): List<Meal> {
        // 1. Authoritative Indian base set IDs
        val indianIds = indianMeals.map { it.id }.toSet()

        // 2. Compute intersection set of allowed IDs
        var allowedIds = indianIds

        if (categoryMealIds != null) {
            allowedIds = allowedIds.intersect(categoryMealIds)
        }

        if (ingredientMealIds != null) {
            allowedIds = allowedIds.intersect(ingredientMealIds)
        }

        // 3. Filter base collection by allowed IDs
        var filteredList = indianMeals.filter { allowedIds.contains(it.id) }

        // 4. Apply Favourites-Only filter
        if (filterState.favouritesOnly) {
            filteredList = filteredList.filter { favouriteIds.contains(it.id) }
        }

        // 5. Apply Meal-Name Search (case-insensitive, trimmed)
        val query = filterState.searchQuery.trim()
        if (query.isNotBlank()) {
            filteredList = filteredList.filter { meal ->
                meal.name.contains(query, ignoreCase = true)
            }
        }

        // 6. Apply Sorting (NAME_ASC / NAME_DESC)
        return when (filterState.sortOrder) {
            SortOrder.NAME_ASC -> filteredList.sortedBy { it.name.lowercase() }
            SortOrder.NAME_DESC -> filteredList.sortedByDescending { it.name.lowercase() }
        }
    }
}
