package com.example.tasteindia.ui.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasteindia.data.repository.MealRepository
import com.example.tasteindia.domain.model.Meal
import com.example.tasteindia.domain.model.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface FavouritesUiState {
    object Loading : FavouritesUiState
    data class Success(val favouriteMeals: List<Meal>) : FavouritesUiState
    object Empty : FavouritesUiState
}

class FavouritesViewModel(
    private val repository: MealRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavouritesUiState>(FavouritesUiState.Loading)
    val uiState: StateFlow<FavouritesUiState> = _uiState.asStateFlow()

    init {
        loadFavourites()
    }

    fun loadFavourites() {
        viewModelScope.launch {
            _uiState.value = FavouritesUiState.Loading

            repository.getFavouriteIds().collect { favouriteIds ->
                if (favouriteIds.isEmpty()) {
                    _uiState.value = FavouritesUiState.Empty
                } else {
                    when (val result = repository.getIndianMeals()) {
                        is Result.Success -> {
                            val savedMeals = result.data.filter { favouriteIds.contains(it.id) }
                            if (savedMeals.isEmpty()) {
                                _uiState.value = FavouritesUiState.Empty
                            } else {
                                _uiState.value = FavouritesUiState.Success(savedMeals)
                            }
                        }
                        is Result.Error -> {
                            _uiState.value = FavouritesUiState.Empty
                        }
                    }
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
