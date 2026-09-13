package com.example.tasteindia.data.repository

import com.example.tasteindia.data.local.PreferencesManager
import com.example.tasteindia.data.remote.MealApi
import com.example.tasteindia.data.remote.NetworkResult
import com.example.tasteindia.data.remote.safeApiCall
import com.example.tasteindia.domain.model.Meal
import com.example.tasteindia.domain.model.MealDetails
import com.example.tasteindia.domain.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Repository interface defining the recipe data contract.
 */
interface MealRepository {
    suspend fun getIndianMeals(): Result<List<Meal>>
    suspend fun getMealDetails(mealId: String): Result<MealDetails>
    suspend fun getMealIdsByCategory(category: String): Result<Set<String>>
    suspend fun getMealIdsByIngredient(ingredient: String): Result<Set<String>>
    suspend fun getAvailableCategories(): Result<List<String>>
    suspend fun getAvailableIngredients(): Result<List<String>>
    fun getFavouriteIds(): Flow<Set<String>>
    suspend fun toggleFavourite(mealId: String)
}

/**
 * Concrete repository implementation managing API requests, local preferences, and DTO mapping.
 */
class DefaultMealRepository(
    private val api: MealApi,
    private val preferencesManager: PreferencesManager? = null
) : MealRepository {

    override suspend fun getIndianMeals(): Result<List<Meal>> {
        return when (val networkResult = safeApiCall { api.getIndianMeals() }) {
            is NetworkResult.Success -> {
                val dtoList = networkResult.data.meals.orEmpty()
                val domainMeals = dtoList.mapNotNull { it.toDomainModel() }
                Result.Success(domainMeals)
            }
            is NetworkResult.HttpError -> {
                Result.Error("HTTP Error ${networkResult.code}: ${networkResult.message ?: "Unknown error"}")
            }
            is NetworkResult.NetworkError -> {
                Result.Error("Network connection failed. Please check your internet connection.", networkResult.throwable)
            }
            is NetworkResult.SerializationError -> {
                Result.Error("Failed to parse recipe data from server.", networkResult.throwable)
            }
            is NetworkResult.UnexpectedError -> {
                Result.Error("An unexpected error occurred.", networkResult.throwable)
            }
        }
    }

    override suspend fun getMealDetails(mealId: String): Result<MealDetails> {
        if (mealId.isBlank()) {
            return Result.Error("Invalid recipe ID")
        }
        return when (val networkResult = safeApiCall { api.getMealDetails(mealId) }) {
            is NetworkResult.Success -> {
                val dto = networkResult.data.meals?.firstOrNull()
                val details = dto?.toDomainModel()
                if (details != null) {
                    Result.Success(details)
                } else {
                    Result.Error("Recipe details not found")
                }
            }
            is NetworkResult.HttpError -> {
                Result.Error("HTTP Error ${networkResult.code}: ${networkResult.message ?: "Unknown error"}")
            }
            is NetworkResult.NetworkError -> {
                Result.Error("Network connection failed. Please check your internet connection.", networkResult.throwable)
            }
            is NetworkResult.SerializationError -> {
                Result.Error("Failed to parse recipe details.", networkResult.throwable)
            }
            is NetworkResult.UnexpectedError -> {
                Result.Error("An unexpected error occurred.", networkResult.throwable)
            }
        }
    }

    override suspend fun getMealIdsByCategory(category: String): Result<Set<String>> {
        if (category.isBlank()) {
            return Result.Success(emptySet())
        }
        return when (val networkResult = safeApiCall { api.getMealsByCategory(category) }) {
            is NetworkResult.Success -> {
                val ids = networkResult.data.meals.orEmpty()
                    .mapNotNull { it.idMeal?.trim() }
                    .filter { it.isNotBlank() }
                    .toSet()
                Result.Success(ids)
            }
            is NetworkResult.HttpError -> Result.Error("HTTP Error ${networkResult.code}")
            is NetworkResult.NetworkError -> Result.Error("Network failure", networkResult.throwable)
            is NetworkResult.SerializationError -> Result.Error("Serialization failure", networkResult.throwable)
            is NetworkResult.UnexpectedError -> Result.Error("Unexpected error", networkResult.throwable)
        }
    }

    override suspend fun getMealIdsByIngredient(ingredient: String): Result<Set<String>> {
        if (ingredient.isBlank()) {
            return Result.Success(emptySet())
        }
        return when (val networkResult = safeApiCall { api.getMealsByIngredient(ingredient) }) {
            is NetworkResult.Success -> {
                val ids = networkResult.data.meals.orEmpty()
                    .mapNotNull { it.idMeal?.trim() }
                    .filter { it.isNotBlank() }
                    .toSet()
                Result.Success(ids)
            }
            is NetworkResult.HttpError -> Result.Error("HTTP Error ${networkResult.code}")
            is NetworkResult.NetworkError -> Result.Error("Network failure", networkResult.throwable)
            is NetworkResult.SerializationError -> Result.Error("Serialization failure", networkResult.throwable)
            is NetworkResult.UnexpectedError -> Result.Error("Unexpected error", networkResult.throwable)
        }
    }

    override suspend fun getAvailableCategories(): Result<List<String>> {
        return when (val networkResult = safeApiCall { api.getCategories() }) {
            is NetworkResult.Success -> {
                val list = networkResult.data.meals.orEmpty()
                    .mapNotNull { it.strCategory?.trim() }
                    .filter { it.isNotBlank() }
                    .sorted()
                Result.Success(list)
            }
            is NetworkResult.HttpError -> Result.Error("HTTP Error ${networkResult.code}")
            is NetworkResult.NetworkError -> Result.Error("Network failure", networkResult.throwable)
            is NetworkResult.SerializationError -> Result.Error("Serialization failure", networkResult.throwable)
            is NetworkResult.UnexpectedError -> Result.Error("Unexpected error", networkResult.throwable)
        }
    }

    override suspend fun getAvailableIngredients(): Result<List<String>> {
        return when (val networkResult = safeApiCall { api.getIngredients() }) {
            is NetworkResult.Success -> {
                val list = networkResult.data.meals.orEmpty()
                    .mapNotNull { it.strIngredient?.trim() }
                    .filter { it.isNotBlank() }
                    .sorted()
                Result.Success(list)
            }
            is NetworkResult.HttpError -> Result.Error("HTTP Error ${networkResult.code}")
            is NetworkResult.NetworkError -> Result.Error("Network failure", networkResult.throwable)
            is NetworkResult.SerializationError -> Result.Error("Serialization failure", networkResult.throwable)
            is NetworkResult.UnexpectedError -> Result.Error("Unexpected error", networkResult.throwable)
        }
    }

    override fun getFavouriteIds(): Flow<Set<String>> {
        return preferencesManager?.favouriteIds ?: flowOf(emptySet())
    }

    override suspend fun toggleFavourite(mealId: String) {
        preferencesManager?.toggleFavourite(mealId)
    }
}
