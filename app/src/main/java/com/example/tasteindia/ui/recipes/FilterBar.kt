package com.example.tasteindia.ui.recipes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tasteindia.domain.model.FilterState
import com.example.tasteindia.domain.model.SortOrder

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterBar(
    filterState: FilterState,
    resultCount: Int,
    availableCategories: List<String>,
    availableIngredients: List<String>,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelect: (String?) -> Unit,
    onIngredientSelect: (String?) -> Unit,
    onFavouritesOnlyToggle: (Boolean) -> Unit,
    onSortOrderSelect: (SortOrder) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var showIngredientDropdown by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Search Input
        OutlinedTextField(
            value = filterState.searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search Indian recipes...") },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = "Search icon")
            },
            trailingIcon = {
                if (filterState.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true
        )

        // Filter Controls Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Selector
            BoxWrapper {
                FilterChip(
                    selected = filterState.selectedCategory != null,
                    onClick = { showCategoryDropdown = true },
                    label = { Text(filterState.selectedCategory ?: "Category") }
                )
                DropdownMenu(
                    expanded = showCategoryDropdown,
                    onDismissRequest = { showCategoryDropdown = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("All Categories") },
                        onClick = {
                            onCategorySelect(null)
                            showCategoryDropdown = false
                        }
                    )
                    availableCategories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                onCategorySelect(category)
                                showCategoryDropdown = false
                            }
                        )
                    }
                }
            }

            // Ingredient Selector
            BoxWrapper {
                FilterChip(
                    selected = filterState.selectedIngredient != null,
                    onClick = { showIngredientDropdown = true },
                    label = { Text(filterState.selectedIngredient ?: "Ingredient") }
                )
                DropdownMenu(
                    expanded = showIngredientDropdown,
                    onDismissRequest = { showIngredientDropdown = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("All Ingredients") },
                        onClick = {
                            onIngredientSelect(null)
                            showIngredientDropdown = false
                        }
                    )
                    availableIngredients.take(25).forEach { ingredient ->
                        DropdownMenuItem(
                            text = { Text(ingredient) },
                            onClick = {
                                onIngredientSelect(ingredient)
                                showIngredientDropdown = false
                            }
                        )
                    }
                }
            }

            // Favourites-only Toggle
            FilterChip(
                selected = filterState.favouritesOnly,
                onClick = { onFavouritesOnlyToggle(!filterState.favouritesOnly) },
                label = { Text("♡ Favourites") }
            )

            IconButton(
                onClick = {
                    val nextSort = if (filterState.sortOrder == SortOrder.NAME_ASC) SortOrder.NAME_DESC else SortOrder.NAME_ASC
                    onSortOrderSelect(nextSort)
                }
            ) {
                Text(
                    text = if (filterState.sortOrder == SortOrder.NAME_ASC) "A-Z" else "Z-A",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Active Filter Chips & Clear All
        if (filterState.isFiltered) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.Center
            ) {
                if (filterState.selectedCategory != null) {
                    AssistChip(
                        onClick = { onCategorySelect(null) },
                        label = { Text("Category: ${filterState.selectedCategory}") },
                        trailingIcon = { Icon(Icons.Filled.Clear, contentDescription = "Remove category filter") }
                    )
                }
                if (filterState.selectedIngredient != null) {
                    AssistChip(
                        onClick = { onIngredientSelect(null) },
                        label = { Text("Ingredient: ${filterState.selectedIngredient}") },
                        trailingIcon = { Icon(Icons.Filled.Clear, contentDescription = "Remove ingredient filter") }
                    )
                }
                if (filterState.favouritesOnly) {
                    AssistChip(
                        onClick = { onFavouritesOnlyToggle(false) },
                        label = { Text("Favourites Only") },
                        trailingIcon = { Icon(Icons.Filled.Clear, contentDescription = "Remove favourites filter") }
                    )
                }
                if (filterState.sortOrder == SortOrder.NAME_DESC) {
                    AssistChip(
                        onClick = { onSortOrderSelect(SortOrder.NAME_ASC) },
                        label = { Text("Sort: Z-A") },
                        trailingIcon = { Icon(Icons.Filled.Clear, contentDescription = "Reset sort order") }
                    )
                }
                TextButton(onClick = onClearAll) {
                    Text("Clear All")
                }
            }
        }

        // Result Count Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (resultCount == 1) "1 recipe" else "$resultCount recipes",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun BoxWrapper(content: @Composable () -> Unit) {
    androidx.compose.foundation.layout.Box {
        content()
    }
}
