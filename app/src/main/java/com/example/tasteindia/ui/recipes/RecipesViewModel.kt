package com.example.tasteindia.ui.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasteindia.data.repository.MealRepository
import com.example.tasteindia.domain.model.Meal
import com.example.tasteindia.domain.model.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

sealed interface RecipesUiState {
    object Loading : RecipesUiState
    data class Success(
        val meals: List<Meal>,
        val favouriteIds: Set<String>
    ) : RecipesUiState
    object Empty : RecipesUiState
    data class Error(val message: String) : RecipesUiState
}

class RecipesViewModel(
    private val repository: MealRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecipesUiState>(RecipesUiState.Loading)
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    init {
        loadRecipes()
    }

    fun loadRecipes() {
        viewModelScope.launch {
            _uiState.value = RecipesUiState.Loading

            when (val result = repository.getIndianMeals()) {
                is Result.Success -> {
                    val meals = result.data
                    if (meals.isEmpty()) {
                        _uiState.value = RecipesUiState.Empty
                    } else {
                        // Combine meals with persistent favourites flow
                        repository.getFavouriteIds().collect { favouriteIds ->
                            _uiState.value = RecipesUiState.Success(
                                meals = meals,
                                favouriteIds = favouriteIds
                            )
                        }
                    }
                }
                is Result.Error -> {
                    _uiState.value = RecipesUiState.Error(
                        message = result.message
                    )
                }
            }
        }
    }

    fun toggleFavourite(mealId: String) {
        viewModelScope.launch {
            repository.toggleFavourite(mealId)
        }
    }
}
