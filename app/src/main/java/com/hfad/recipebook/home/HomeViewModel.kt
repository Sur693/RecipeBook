package com.hfad.recipebook.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.recipebook.data.remote.FoodApi
import com.hfad.recipebook.data.remote.MealsResponse
import com.hfad.recipebook.model.RecipePreview
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import com.hfad.recipebook.data.converters.DataConverter

internal class HomeViewModel(
    private val converter: DataConverter
) : ViewModel(){

    private val _homeScreenState = MutableStateFlow<List<RecipePreview>>(listOf())
    val homeScreenState = _homeScreenState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    init{
        viewModelScope.launch{
            val allMeals: List<RecipePreview> = getRecipes()
                .mapNotNull { it.meals }
                .flatten()
                .map { converter.mealDataModelToPreview(it) }
            _homeScreenState.value = allMeals
            Log.d("HomeViewModel", "Recipes loaded: ${allMeals.size}")
        }
    }

    private suspend fun getRecipes(): List<MealsResponse> {
        val results = mutableListOf<Deferred<MealsResponse>>()
        repeat(10) {
            val deferred = viewModelScope.async(Dispatchers.IO){
                FoodApi.retrofitService.getRandomMeal()
            }
            results.add(deferred)
        }
        return results.awaitAll()
    }

    fun searchRecipes(query: String) {
        _searchQuery.value = query  // 1. Сохраняем текст поиска

        if (query.isBlank()) {      // 2. Если поле пустое
            _homeScreenState.value = emptyList()
            return
        }

        viewModelScope.launch {      // 3. Запускаем корутину (асинхронный код)
            try {
                val response = FoodApi.retrofitService.searchMealsByName(query)  // 4. Делаем запрос к API
                val meals = response.meals?.map { converter.mealDataModelToPreview(it) } ?: emptyList()  // 5. Конвертируем в RecipePreview
                _homeScreenState.value = meals  // 6. Обновляем список рецептов
            } catch (e: Exception) {
                _homeScreenState.value = emptyList()  // 7. Если ошибка → показываем пустой список
            }
        }
    }
}