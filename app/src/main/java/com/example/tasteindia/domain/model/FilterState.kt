package com.example.tasteindia.domain.model

enum class SortOrder {
    NAME_ASC,
    NAME_DESC
}

data class FilterState(
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val selectedIngredient: String? = null,
    val favouritesOnly: Boolean = false,
    val sortOrder: SortOrder = SortOrder.NAME_ASC
) {
    val isFiltered: Boolean
        get() = searchQuery.isNotBlank() || selectedCategory != null || selectedIngredient != null || favouritesOnly || sortOrder != SortOrder.NAME_ASC
}
