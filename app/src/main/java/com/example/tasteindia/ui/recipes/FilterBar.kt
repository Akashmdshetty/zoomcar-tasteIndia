package com.example.tasteindia.ui.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.tasteindia.domain.model.FilterState
import com.example.tasteindia.domain.model.SortOrder
import com.example.tasteindia.ui.theme.SpicePrimary
import com.example.tasteindia.ui.theme.SpicePrimaryFixed
import com.example.tasteindia.ui.theme.SpiceSurfaceContainer
import com.example.tasteindia.ui.theme.SpiceSurfaceContainerLow

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
    val keyboardController = LocalSoftwareKeyboardController.current
    val filterScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        // Search Input Bar (Pill Shape)
        OutlinedTextField(
            value = filterState.searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            placeholder = {
                Text(
                    text = "Search Indian recipes...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search icon",
                    tint = SpicePrimary,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (filterState.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = "Clear search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(100.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SpiceSurfaceContainerLow,
                unfocusedContainerColor = SpiceSurfaceContainerLow,
                disabledContainerColor = SpiceSurfaceContainerLow,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() })
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(filterScrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Dropdown Filter Chip
            Box {
                FilterChip(
                    selected = filterState.selectedCategory != null,
                    onClick = { showCategoryDropdown = true },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(filterState.selectedCategory ?: "Category")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    },
                    shape = RoundedCornerShape(100.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = SpiceSurfaceContainer,
                        selectedContainerColor = SpicePrimaryFixed
                    )
                )

                DropdownMenu(
                    expanded = showCategoryDropdown,
                    onDismissRequest = { showCategoryDropdown = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("All Categories", fontWeight = FontWeight.Bold) },
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

            // Ingredient Dropdown Filter Chip
            Box {
                FilterChip(
                    selected = filterState.selectedIngredient != null,
                    onClick = { showIngredientDropdown = true },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(filterState.selectedIngredient ?: "Ingredient")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    },
                    shape = RoundedCornerShape(100.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = SpiceSurfaceContainer,
                        selectedContainerColor = SpicePrimaryFixed
                    )
                )

                DropdownMenu(
                    expanded = showIngredientDropdown,
                    onDismissRequest = { showIngredientDropdown = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("All Ingredients", fontWeight = FontWeight.Bold) },
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

            // Favourites-only Filter Chip
            FilterChip(
                selected = filterState.favouritesOnly,
                onClick = { onFavouritesOnlyToggle(!filterState.favouritesOnly) },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (filterState.favouritesOnly) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Favourites")
                    }
                },
                shape = RoundedCornerShape(100.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = SpiceSurfaceContainer,
                    selectedContainerColor = MaterialTheme.colorScheme.errorContainer
                )
            )

            // Sort Toggle Button
            Surface(
                onClick = {
                    val nextSort = if (filterState.sortOrder == SortOrder.NAME_ASC) SortOrder.NAME_DESC else SortOrder.NAME_ASC
                    onSortOrderSelect(nextSort)
                },
                shape = RoundedCornerShape(100.dp),
                color = SpiceSurfaceContainer,
                modifier = Modifier.height(32.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (filterState.sortOrder == SortOrder.NAME_ASC) "Sort: A-Z" else "Sort: Z-A",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Active Filter Chips & Clear All Button
        if (filterState.isFiltered) {
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.Center
            ) {
                if (filterState.selectedCategory != null) {
                    AssistChip(
                        onClick = { onCategorySelect(null) },
                        label = { Text(filterState.selectedCategory) },
                        trailingIcon = { Icon(Icons.Filled.Close, contentDescription = "Remove category filter", modifier = Modifier.size(14.dp)) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = SpicePrimaryFixed)
                    )
                }
                if (filterState.selectedIngredient != null) {
                    AssistChip(
                        onClick = { onIngredientSelect(null) },
                        label = { Text(filterState.selectedIngredient) },
                        trailingIcon = { Icon(Icons.Filled.Close, contentDescription = "Remove ingredient filter", modifier = Modifier.size(14.dp)) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = SpicePrimaryFixed)
                    )
                }
                if (filterState.favouritesOnly) {
                    AssistChip(
                        onClick = { onFavouritesOnlyToggle(false) },
                        label = { Text("Favourites Only") },
                        trailingIcon = { Icon(Icons.Filled.Close, contentDescription = "Remove favourites filter", modifier = Modifier.size(14.dp)) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    )
                }
                if (filterState.sortOrder == SortOrder.NAME_DESC) {
                    AssistChip(
                        onClick = { onSortOrderSelect(SortOrder.NAME_ASC) },
                        label = { Text("Sort: Z-A") },
                        trailingIcon = { Icon(Icons.Filled.Close, contentDescription = "Reset sort order", modifier = Modifier.size(14.dp)) }
                    )
                }
                TextButton(onClick = onClearAll) {
                    Text(
                        text = "Clear All",
                        style = MaterialTheme.typography.labelMedium,
                        color = SpicePrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Result Count & Live Sync Header Line
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Showing $resultCount delicious recipes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(SpiceSurfaceContainer)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "Live Sync",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
