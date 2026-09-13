package com.example.tasteindia.data.repository

import com.example.tasteindia.data.remote.MealApi
import com.example.tasteindia.data.remote.NetworkResult
import com.example.tasteindia.data.remote.safeApiCall
import com.example.tasteindia.domain.model.Meal
import com.example.tasteindia.domain.model.Result

/**
 * Repository interface defining the Indian recipe data contract.
 * Enables deterministic test doubles for unit testing.
 */
interface MealRepository {
    /**
     * Fetches the authoritative Indian meal base collection.
     */
    suspend fun getIndianMeals(): Result<List<Meal>>
}

/**
 * Concrete repository implementation managing API requests and DTO mapping.
 */
class DefaultMealRepository(
    private val api: MealApi
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
                Result.Error("Network connection failed", networkResult.throwable)
            }
            is NetworkResult.SerializationError -> {
                Result.Error("Failed to parse API response", networkResult.throwable)
            }
            is NetworkResult.UnexpectedError -> {
                Result.Error("An unexpected error occurred", networkResult.throwable)
            }
        }
    }
}
