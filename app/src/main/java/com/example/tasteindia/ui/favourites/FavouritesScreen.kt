package com.example.tasteindia.ui.favourites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tasteindia.domain.model.Meal
import com.example.tasteindia.ui.recipes.RecipeItem

@Composable
fun FavouritesScreen(
    uiState: FavouritesUiState,
    onRecipeClick: (String) -> Unit,
    onFavouriteToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            is FavouritesUiState.Loading -> {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            is FavouritesUiState.Empty -> {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No favourite recipes yet.",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Start saving recipes you love by tapping the heart icon.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
            is FavouritesUiState.Success -> {
                FavouritesListContent(
                    meals = uiState.favouriteMeals,
                    onRecipeClick = onRecipeClick,
                    onFavouriteToggle = onFavouriteToggle
                )
            }
        }
    }
}

@Composable
private fun FavouritesListContent(
    meals: List<Meal>,
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
                isFavourite = true,
                onRecipeClick = onRecipeClick,
                onFavouriteToggle = onFavouriteToggle
            )
        }
    }
}
