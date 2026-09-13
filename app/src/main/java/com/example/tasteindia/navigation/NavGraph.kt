package com.example.tasteindia.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tasteindia.ui.details.DetailScreen
import com.example.tasteindia.ui.details.RecipeDetailsViewModel
import com.example.tasteindia.ui.favourites.FavouritesScreen
import com.example.tasteindia.ui.favourites.FavouritesViewModel
import com.example.tasteindia.ui.recipes.RecipesScreen
import com.example.tasteindia.ui.recipes.RecipesViewModel

object Destinations {
    const val RECIPES = "recipes"
    const val FAVOURITES = "favourites"
    const val DETAIL = "detail/{recipeId}"

    fun detailRoute(recipeId: String): String = "detail/$recipeId"
}

@Composable
fun TasteIndiaNavGraph(
    navController: NavHostController,
    recipesViewModel: RecipesViewModel,
    favouritesViewModel: FavouritesViewModel,
    detailsViewModel: RecipeDetailsViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Destinations.RECIPES,
        modifier = modifier
    ) {
        composable(Destinations.RECIPES) {
            val uiState by recipesViewModel.uiState.collectAsStateWithLifecycle()
            RecipesScreen(
                uiState = uiState,
                onRecipeClick = { recipeId ->
                    navController.navigate(Destinations.detailRoute(recipeId))
                },
                onFavouriteToggle = { recipeId ->
                    recipesViewModel.toggleFavourite(recipeId)
                },
                onSearchQueryChange = { query ->
                    recipesViewModel.onSearchQueryChange(query)
                },
                onCategorySelect = { category ->
                    recipesViewModel.onCategorySelect(category)
                },
                onIngredientSelect = { ingredient ->
                    recipesViewModel.onIngredientSelect(ingredient)
                },
                onFavouritesOnlyToggle = { enabled ->
                    recipesViewModel.onFavouritesOnlyToggle(enabled)
                },
                onSortOrderSelect = { sortOrder ->
                    recipesViewModel.onSortOrderSelect(sortOrder)
                },
                onClearAll = {
                    recipesViewModel.onClearAll()
                },
                onRetry = {
                    recipesViewModel.loadData()
                }
            )
        }

        composable(Destinations.FAVOURITES) {
            val uiState by favouritesViewModel.uiState.collectAsStateWithLifecycle()
            FavouritesScreen(
                uiState = uiState,
                onRecipeClick = { recipeId ->
                    navController.navigate(Destinations.detailRoute(recipeId))
                },
                onFavouriteToggle = { recipeId ->
                    favouritesViewModel.toggleFavourite(recipeId)
                }
            )
        }

        composable(Destinations.DETAIL) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
            val uiState by detailsViewModel.uiState.collectAsStateWithLifecycle()

            DetailScreen(
                recipeId = recipeId,
                uiState = uiState,
                onLoadDetails = { id ->
                    detailsViewModel.loadDetails(id)
                },
                onFavouriteToggle = {
                    detailsViewModel.toggleFavourite()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
