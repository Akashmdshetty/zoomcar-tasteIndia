package com.example.tasteindia.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasteindia.data.repository.MealRepository
import com.example.tasteindia.domain.model.MealDetails
import com.example.tasteindia.domain.model.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface RecipeDetailsUiState {
    object Loading : RecipeDetailsUiState
    data class Success(
        val details: MealDetails,
        val isFavourite: Boolean
    ) : RecipeDetailsUiState
    data class Error(val message: String) : RecipeDetailsUiState
}

class RecipeDetailsViewModel(
    private val repository: MealRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecipeDetailsUiState>(RecipeDetailsUiState.Loading)
    val uiState: StateFlow<RecipeDetailsUiState> = _uiState.asStateFlow()

    private var currentMealId: String = ""

    fun loadDetails(mealId: String) {
        currentMealId = mealId
        viewModelScope.launch {
            _uiState.value = RecipeDetailsUiState.Loading

            when (val result = repository.getMealDetails(mealId)) {
                is Result.Success -> {
                    val details = result.data
                    repository.getFavouriteIds().collect { favouriteIds ->
                        _uiState.value = RecipeDetailsUiState.Success(
                            details = details,
                            isFavourite = favouriteIds.contains(mealId)
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.value = RecipeDetailsUiState.Error(
                        message = result.message
                    )
                }
            }
        }
    }

    fun toggleFavourite() {
        if (currentMealId.isNotBlank()) {
            viewModelScope.launch {
                repository.toggleFavourite(currentMealId)
            }
        }
    }
}
