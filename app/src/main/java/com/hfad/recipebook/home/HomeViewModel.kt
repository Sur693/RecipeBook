package com.hfad.recipebook.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.recipebook.data.remote.FoodApi
import com.hfad.recipebook.data.remote.MealDataModel
import com.hfad.recipebook.data.remote.MealsResponse
import com.hfad.recipebook.model.RecipePreview
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import com.hfad.recipebook.R
import com.hfad.recipebook.data.converters.DataConverter

internal class HomeViewModel(
    private val converter: DataConverter
) : ViewModel(){

    private val _homeScreenState = MutableStateFlow<List<RecipePreview>>(listOf())
    val homeScreenState = _homeScreenState.asStateFlow()

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
        repeat(1) {
            val deferred = viewModelScope.async(Dispatchers.IO){
                FoodApi.retrofitService.getRandomMeal()
            }
            results.add(deferred)
        }
        return results.awaitAll()
    }
}