package com.example.tasteindia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tasteindia.data.local.DefaultPreferencesManager
import com.example.tasteindia.data.remote.RetrofitClient
import com.example.tasteindia.data.repository.DefaultMealRepository
import com.example.tasteindia.navigation.Destinations
import com.example.tasteindia.navigation.TasteIndiaNavGraph
import com.example.tasteindia.ui.details.RecipeDetailsViewModel
import com.example.tasteindia.ui.favourites.FavouritesViewModel
import com.example.tasteindia.ui.recipes.RecipesViewModel
import com.example.tasteindia.ui.theme.SpicePrimary
import com.example.tasteindia.ui.theme.SpicePrimaryFixed
import com.example.tasteindia.ui.theme.SpiceSurfaceContainerLowest
import com.example.tasteindia.ui.theme.TasteIndiaTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val preferencesManager = DefaultPreferencesManager(applicationContext)
        val repository = DefaultMealRepository(
            api = RetrofitClient.mealApi,
            preferencesManager = preferencesManager
        )

        setContent {
            val recipesViewModel = remember { RecipesViewModel(repository) }
            val favouritesViewModel = remember { FavouritesViewModel(repository) }
            val detailsViewModel = remember { RecipeDetailsViewModel(repository) }

            TasteIndiaTheme {
                TasteIndiaMainContainer(
                    recipesViewModel = recipesViewModel,
                    favouritesViewModel = favouritesViewModel,
                    detailsViewModel = detailsViewModel
                )
            }
        }
    }
}

@Composable
fun TasteIndiaMainContainer(
    recipesViewModel: RecipesViewModel,
    favouritesViewModel: FavouritesViewModel,
    detailsViewModel: RecipeDetailsViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute == Destinations.RECIPES || currentRoute == Destinations.FAVOURITES

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = SpiceSurfaceContainerLowest
                ) {
                    NavigationBarItem(
                        selected = currentRoute == Destinations.RECIPES,
                        onClick = {
                            navController.navigate(Destinations.RECIPES) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Filled.Menu, contentDescription = "Recipes") },
                        label = { Text("Recipes") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SpicePrimary,
                            selectedTextColor = SpicePrimary,
                            indicatorColor = SpicePrimaryFixed
                        )
                    )
                    NavigationBarItem(
                        selected = currentRoute == Destinations.FAVOURITES,
                        onClick = {
                            navController.navigate(Destinations.FAVOURITES) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = "Favourites") },
                        label = { Text("Favourites") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SpicePrimary,
                            selectedTextColor = SpicePrimary,
                            indicatorColor = SpicePrimaryFixed
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        TasteIndiaNavGraph(
            navController = navController,
            recipesViewModel = recipesViewModel,
            favouritesViewModel = favouritesViewModel,
            detailsViewModel = detailsViewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
