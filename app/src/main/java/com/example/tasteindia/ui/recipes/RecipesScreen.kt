package com.example.tasteindia.ui.recipes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tasteindia.domain.model.FilterState
import com.example.tasteindia.domain.model.Meal
import com.example.tasteindia.domain.model.SortOrder

@Composable
fun RecipesScreen(
    uiState: RecipesUiState,
    onRecipeClick: (String) -> Unit,
    onFavouriteToggle: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelect: (String?) -> Unit,
    onIngredientSelect: (String?) -> Unit,
    onFavouritesOnlyToggle: (Boolean) -> Unit,
    onSortOrderSelect: (SortOrder) -> Unit,
    onClearAll: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        if (uiState is RecipesUiState.Success) {
            FilterBar(
                filterState = uiState.filterState,
                resultCount = uiState.visibleMeals.size,
                availableCategories = uiState.availableCategories,
                availableIngredients = uiState.availableIngredients,
                onSearchQueryChange = onSearchQueryChange,
                onCategorySelect = onCategorySelect,
                onIngredientSelect = onIngredientSelect,
                onFavouritesOnlyToggle = onFavouritesOnlyToggle,
                onSortOrderSelect = onSortOrderSelect,
                onClearAll = onClearAll
            )
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is RecipesUiState.Loading -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading recipes...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                is RecipesUiState.Empty -> {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No recipes found.",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                is RecipesUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Unable to load recipes.",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onRetry) {
                            Text(text = "Retry")
                        }
                    }
                }
                is RecipesUiState.Success -> {
                    if (uiState.isFilterEmpty) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No recipes match your filters.",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedButton(onClick = onClearAll) {
                                Text("Clear All Filters")
                            }
                        }
                    } else {
                        RecipeListContent(
                            meals = uiState.visibleMeals,
                            favouriteIds = uiState.favouriteIds,
                            onRecipeClick = onRecipeClick,
                            onFavouriteToggle = onFavouriteToggle
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeListContent(
    meals: List<Meal>,
    favouriteIds: Set<String>,
    onRecipeClick: (String) -> Unit,
    onFavouriteToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = meals,
            key = { meal -> meal.id }
        ) { meal ->
            RecipeItem(
                meal = meal,
                isFavourite = favouriteIds.contains(meal.id),
                onRecipeClick = onRecipeClick,
                onFavouriteToggle = onFavouriteToggle
            )
        }
    }
}
