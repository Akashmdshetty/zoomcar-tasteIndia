package com.example.tasteindia.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tasteindia.ui.details.DetailScreen
import com.example.tasteindia.ui.favourites.FavouritesScreen
import com.example.tasteindia.ui.filters.FilterScreen
import com.example.tasteindia.ui.recipes.RecipesScreen

object Destinations {
    const val RECIPES = "recipes"
    const val DETAIL = "detail/{recipeId}"
    const val FILTERS = "filters"
    const val FAVOURITES = "favourites"
}

@Composable
fun TasteIndiaNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Destinations.RECIPES,
        modifier = modifier
    ) {
        composable(Destinations.RECIPES) {
            RecipesScreen()
        }
        composable(Destinations.DETAIL) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
            DetailScreen(recipeId = recipeId)
        }
        composable(Destinations.FILTERS) {
            FilterScreen()
        }
        composable(Destinations.FAVOURITES) {
            FavouritesScreen()
        }
    }
}
