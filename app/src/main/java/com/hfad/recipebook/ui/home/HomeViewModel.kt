package com.hfad.recipebook.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.recipebook.data.remote.MealsResponse
import com.hfad.recipebook.domain.model.RecipePreview
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import com.hfad.recipebook.data.converter.DataConverter
import com.hfad.recipebook.data.remote.FoodService
import com.hfad.recipebook.domain.model.FilterType
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val converter: DataConverter,
    private val foodService: FoodService
) : ViewModel() {

    private val _homeScreenState = MutableStateFlow<List<RecipePreview>>(listOf())
    val homeScreenState = _homeScreenState.asStateFlow()

    //поисковик
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _activeFilter = MutableStateFlow<Pair<FilterType, String>?>(null)
    val activeFilter = _activeFilter.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    init {
        loadRandomRecipes()
    }

    private fun loadRandomRecipes(){
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val allMeals: List<RecipePreview> = getRecipes()
                    .mapNotNull { it.meals }
                    .flatten()
                    .map { converter.mealDataModelToPreview(it) }

                _homeScreenState.value = allMeals
                Log.d("HomeViewModel", "Recipes loaded: ${allMeals.size}")

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading recipes", e)
                _homeScreenState.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }


    // получение рецепта
    private suspend fun getRecipes(): List<MealsResponse> = try {
        val results = mutableListOf<Deferred<MealsResponse>>()

        repeat(10) {
            val deferred = viewModelScope.async(Dispatchers.IO) {
                foodService.getRandomMeal()
            }
            results.add(deferred)
        }

        results.awaitAll()

    } catch (e: Exception) {
        Log.e("HomeViewModel", "Error in getRecipes()", e)
        emptyList()
    }


    // поиск рецепта
    fun searchRecipes(query: String) {
        _searchQuery.value = query  // сохраняем текст поиска
        _activeFilter.value = null

        viewModelScope.launch {
            _isLoading.value = true

            try {
                val response = foodService.searchMealsByName(query)  // запрос к API
                val meals = response.meals?.map { converter.mealDataModelToPreview(it) } ?: emptyList()  // конвертируем в RecipePreview
                _homeScreenState.value = meals  // обновляем список рецептов
            } catch (e: Exception) {
                _homeScreenState.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }


    // поиск по фильтру
    fun searchByFilter(filterValue: String, filterType: FilterType){
        viewModelScope.launch {
            _isLoading.value = true
            _searchQuery.value = ""

            try {
                val response = when (filterType) {
                    FilterType.AREA -> foodService.filterByArea(filterValue)
                    FilterType.CATEGORY -> foodService.filterByCategory(filterValue)
                    FilterType.INGREDIENT -> foodService.filterByIngredients(filterValue)
                }
                val mealsPreview: List<RecipePreview> = response.meals?.mapNotNull { mealSummary ->
                    val fullMeal = foodService.getRecipeById(mealSummary.idMeal)
                    fullMeal.meals?.firstOrNull()?.let { converter.mealDataModelToPreview(it) }
                } ?: emptyList()

                _homeScreenState.value = mealsPreview
                _activeFilter.value = filterType to filterValue
            } catch (e: Exception) {
                _homeScreenState.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearFilter() {
        _activeFilter.value = null
        loadRandomRecipes()
    }
}