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
 * Enables deterministic test doubles for unit testing.
 */
interface MealRepository {
    suspend fun getIndianMeals(): Result<List<Meal>>
    suspend fun getMealDetails(mealId: String): Result<MealDetails>
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

    override fun getFavouriteIds(): Flow<Set<String>> {
        return preferencesManager?.favouriteIds ?: flowOf(emptySet())
    }

    override suspend fun toggleFavourite(mealId: String) {
        preferencesManager?.toggleFavourite(mealId)
    }
}
