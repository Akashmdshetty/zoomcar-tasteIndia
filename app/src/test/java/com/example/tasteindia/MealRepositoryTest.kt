package com.example.tasteindia

import com.example.tasteindia.data.remote.MealApi
import com.example.tasteindia.data.remote.RetrofitClient
import com.example.tasteindia.data.remote.dto.MealDetailsResponseDto
import com.example.tasteindia.data.remote.dto.MealFilterResponseDto
import com.example.tasteindia.data.remote.dto.MealSummaryDto
import com.example.tasteindia.data.repository.DefaultMealRepository
import com.example.tasteindia.data.repository.toDomainModel
import com.example.tasteindia.domain.model.Result
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class MealRepositoryTest {

    @Test
    fun mealSummaryDto_toDomainModel_validDto_returnsMeal() {
        val dto = MealSummaryDto(
            idMeal = "52785",
            strMeal = "Dal Makhani",
            strMealThumb = "https://www.themealdb.com/images/media/meals/v28wo51619787382.jpg"
        )

        val domainMeal = dto.toDomainModel()

        assertNotNull(domainMeal)
        assertEquals("52785", domainMeal?.id)
        assertEquals("Dal Makhani", domainMeal?.name)
        assertEquals("https://www.themealdb.com/images/media/meals/v28wo51619787382.jpg", domainMeal?.thumbnailUrl)
    }

    @Test
    fun mealSummaryDto_toDomainModel_blankIdOrName_returnsNull() {
        val invalidDto = MealSummaryDto(
            idMeal = "",
            strMeal = "Butter Chicken",
            strMealThumb = "http://example.com/image.jpg"
        )

        assertNull(invalidDto.toDomainModel())
    }

    @Test
    fun defaultMealRepository_getIndianMeals_mapsFakeResponseToDomainMeals() = runBlocking {
        val fakeApi = object : MealApi {
            override suspend fun getIndianMeals(): Response<MealFilterResponseDto> {
                val dtoList = listOf(
                    MealSummaryDto(idMeal = "52785", strMeal = "Dal Makhani", strMealThumb = "https://example.com/dal.jpg"),
                    MealSummaryDto(idMeal = "52807", strMeal = "Baingan Bharta", strMealThumb = "https://example.com/baingan.jpg")
                )
                return Response.success(MealFilterResponseDto(meals = dtoList))
            }

            override suspend fun getMealDetails(mealId: String): Response<MealDetailsResponseDto> {
                return Response.success(MealDetailsResponseDto(meals = emptyList()))
            }
        }

        val repository = DefaultMealRepository(fakeApi)
        val result = repository.getIndianMeals()

        assertTrue(result is Result.Success)
        val meals = (result as Result.Success).data
        assertEquals(2, meals.size)
        assertEquals("52785", meals[0].id)
        assertEquals("Dal Makhani", meals[0].name)
    }

    @Test
    fun defaultMealRepository_getIndianMeals_handlesEmptyNullResponseGracefully() = runBlocking {
        val fakeApi = object : MealApi {
            override suspend fun getIndianMeals(): Response<MealFilterResponseDto> {
                return Response.success(MealFilterResponseDto(meals = null))
            }

            override suspend fun getMealDetails(mealId: String): Response<MealDetailsResponseDto> {
                return Response.success(MealDetailsResponseDto(meals = null))
            }
        }

        val repository = DefaultMealRepository(fakeApi)
        val result = repository.getIndianMeals()

        assertTrue(result is Result.Success)
        val meals = (result as Result.Success).data
        assertTrue(meals.isEmpty())
    }

    @Test
    fun defaultMealRepository_getIndianMeals_realEndpoint_executesWithoutCrashing() = runBlocking {
        val repository = DefaultMealRepository(RetrofitClient.mealApi)

        val result = repository.getIndianMeals()

        assertTrue("Expected Result.Success from endpoint call", result is Result.Success)
        val meals = (result as Result.Success).data
        println("Real API returned ${meals.size} Indian meals.")
    }
}
