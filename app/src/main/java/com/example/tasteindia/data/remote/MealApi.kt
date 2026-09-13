package com.example.tasteindia.data.remote

import com.example.tasteindia.data.remote.dto.CategoryListResponseDto
import com.example.tasteindia.data.remote.dto.IngredientListResponseDto
import com.example.tasteindia.data.remote.dto.MealDetailsResponseDto
import com.example.tasteindia.data.remote.dto.MealFilterResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service definition for TheMealDB API.
 */
interface MealApi {

    /**
     * Obtains the collection of Indian meals (authoritative base set).
     * Endpoint: filter.php?a=Indian
     */
    @GET("filter.php?a=Indian")
    suspend fun getIndianMeals(): Response<MealFilterResponseDto>

    /**
     * Obtains complete details for a specific meal.
     * Endpoint: lookup.php?i={mealId}
     */
    @GET("lookup.php")
    suspend fun getMealDetails(
        @Query("i") mealId: String
    ): Response<MealDetailsResponseDto>

    /**
     * Obtains meals filtered by category.
     * Endpoint: filter.php?c={category}
     */
    @GET("filter.php")
    suspend fun getMealsByCategory(
        @Query("c") category: String
    ): Response<MealFilterResponseDto>

    /**
     * Obtains meals filtered by main ingredient.
     * Endpoint: filter.php?i={ingredient}
     */
    @GET("filter.php")
    suspend fun getMealsByIngredient(
        @Query("i") ingredient: String
    ): Response<MealFilterResponseDto>

    /**
     * Obtains list of available categories.
     * Endpoint: list.php?c=list
     */
    @GET("list.php?c=list")
    suspend fun getCategories(): Response<CategoryListResponseDto>

    /**
     * Obtains list of available main ingredients.
     * Endpoint: list.php?i=list
     */
    @GET("list.php?i=list")
    suspend fun getIngredients(): Response<IngredientListResponseDto>
}
