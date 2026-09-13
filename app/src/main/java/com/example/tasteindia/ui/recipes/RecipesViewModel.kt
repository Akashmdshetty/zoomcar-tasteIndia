package com.example.tasteindia.ui.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasteindia.data.repository.MealFilterEngine
import com.example.tasteindia.data.repository.MealRepository
import com.example.tasteindia.domain.model.FilterState
import com.example.tasteindia.domain.model.Meal
import com.example.tasteindia.domain.model.Result
import com.example.tasteindia.domain.model.SortOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

sealed interface RecipesUiState {
    object Loading : RecipesUiState
    data class Success(
        val allIndianMeals: List<Meal>,
        val visibleMeals: List<Meal>,
        val favouriteIds: Set<String>,
        val filterState: FilterState,
        val availableCategories: List<String>,
        val availableIngredients: List<String>,
        val isFilterEmpty: Boolean
    ) : RecipesUiState
    object Empty : RecipesUiState
    data class Error(val message: String) : RecipesUiState
}

class RecipesViewModel(
    private val repository: MealRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecipesUiState>(RecipesUiState.Loading)
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    private val filterStateFlow = MutableStateFlow(FilterState())

    private var allIndianMeals: List<Meal> = emptyList()
    private var availableCategories: List<String> = emptyList()
    private var availableIngredients: List<String> = emptyList()

    private var currentCategoryMealIds: Set<String>? = null
    private var currentIngredientMealIds: Set<String>? = null

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = RecipesUiState.Loading

            // Fetch categories and ingredients for filter options in background
            launch {
                if (let { repository.getAvailableCategories() } is Result.Success) {
                    availableCategories = (repository.getAvailableCategories() as? Result.Success)?.data.orEmpty()
                }
                if (let { repository.getAvailableIngredients() } is Result.Success) {
                    availableIngredients = (repository.getAvailableIngredients() as? Result.Success)?.data.orEmpty()
                }
            }

            when (val result = repository.getIndianMeals()) {
                is Result.Success -> {
                    allIndianMeals = result.data
                    if (allIndianMeals.isEmpty()) {
                        _uiState.value = RecipesUiState.Empty
                    } else {
                        // Observe favourites and filterState together
                        combine(
                            repository.getFavouriteIds(),
                            filterStateFlow
                        ) { favouriteIds, filterState ->
                            val visible = MealFilterEngine.applyFilters(
                                indianMeals = allIndianMeals,
                                filterState = filterState,
                                categoryMealIds = currentCategoryMealIds,
                                ingredientMealIds = currentIngredientMealIds,
                                favouriteIds = favouriteIds
                            )

                            RecipesUiState.Success(
                                allIndianMeals = allIndianMeals,
                                visibleMeals = visible,
                                favouriteIds = favouriteIds,
                                filterState = filterState,
                                availableCategories = availableCategories,
                                availableIngredients = availableIngredients,
                                isFilterEmpty = visible.isEmpty() && filterState.isFiltered
                            )
                        }.collect { state ->
                            _uiState.value = state
                        }
                    }
                }
                is Result.Error -> {
                    _uiState.value = RecipesUiState.Error(result.message)
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        filterStateFlow.value = filterStateFlow.value.copy(searchQuery = query)
    }

    fun onCategorySelect(category: String?) {
        viewModelScope.launch {
            if (category.isNullOrBlank()) {
                currentCategoryMealIds = null
                filterStateFlow.value = filterStateFlow.value.copy(selectedCategory = null)
            } else {
                filterStateFlow.value = filterStateFlow.value.copy(selectedCategory = category)
                when (val res = repository.getMealIdsByCategory(category)) {
                    is Result.Success -> currentCategoryMealIds = res.data
                    is Result.Error -> currentCategoryMealIds = emptySet()
                }
                // Trigger re-filtering
                filterStateFlow.value = filterStateFlow.value.copy()
            }
        }
    }

    fun onIngredientSelect(ingredient: String?) {
        viewModelScope.launch {
            if (ingredient.isNullOrBlank()) {
                currentIngredientMealIds = null
                filterStateFlow.value = filterStateFlow.value.copy(selectedIngredient = null)
            } else {
                filterStateFlow.value = filterStateFlow.value.copy(selectedIngredient = ingredient)
                when (val res = repository.getMealIdsByIngredient(ingredient)) {
                    is Result.Success -> currentIngredientMealIds = res.data
                    is Result.Error -> currentIngredientMealIds = emptySet()
                }
                // Trigger re-filtering
                filterStateFlow.value = filterStateFlow.value.copy()
            }
        }
    }

    fun onFavouritesOnlyToggle(enabled: Boolean) {
        filterStateFlow.value = filterStateFlow.value.copy(favouritesOnly = enabled)
    }

    fun onSortOrderSelect(sortOrder: SortOrder) {
        filterStateFlow.value = filterStateFlow.value.copy(sortOrder = sortOrder)
    }

    fun onClearAll() {
        currentCategoryMealIds = null
        currentIngredientMealIds = null
        filterStateFlow.value = FilterState()
    }

    fun toggleFavourite(mealId: String) {
        viewModelScope.launch {
            repository.toggleFavourite(mealId)
        }
    }
}
