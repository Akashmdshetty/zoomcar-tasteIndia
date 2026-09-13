package com.example.tasteindia.data.remote

import com.example.tasteindia.data.remote.dto.MealFilterResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service definition for TheMealDB API.
 */
interface MealApi {

    /**
     * Obtains the collection of Indian meals.
     * Endpoint: filter.php?a=Indian
     */
    @GET("filter.php?a=Indian")
    suspend fun getIndianMeals(): Response<MealFilterResponseDto>

    /**
     * Signature prepared for future lookup endpoint (Milestone for details).
     * Endpoint: lookup.php?i={mealId}
     */
    @GET("lookup.php")
    suspend fun getMealDetails(
        @Query("i") mealId: String
    ): Response<Unit> // Signature prepared for later lookup implementation
}
