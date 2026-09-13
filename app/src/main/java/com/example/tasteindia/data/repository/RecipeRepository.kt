package com.example.tasteindia.data.repository

import com.example.tasteindia.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining repository contract for recipe operations.
 * Enables clean separation and test doubles.
 */
interface RecipeRepository {
    fun getRecipes(): Flow<List<Recipe>>
    fun getFavourites(): Flow<Set<String>>
    suspend fun toggleFavourite(recipeId: String)
}
