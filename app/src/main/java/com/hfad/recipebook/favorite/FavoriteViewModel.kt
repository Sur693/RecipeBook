package com.hfad.recipebook.favorite

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.recipebook.data.converters.DataConverter
import com.hfad.recipebook.data.remote.FoodApi
import com.hfad.recipebook.data.remote.MealsResponse
import com.hfad.recipebook.model.RecipePreview
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow

import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.launch

class FavoriteViewModel (
    private val converter: DataConverter,
    private val repository: FavoritesRepository
    ) : ViewModel(){
    private val _favoriteScreenState = MutableStateFlow<List<RecipePreview>>(listOf())
    val favoriteScreenState = _favoriteScreenState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val favorites = repository.favorites

    init {
        loadRecipes()
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            repository.favorites.collect { favoriteIds ->
                // ← ИСПРАВЛЕНИЕ: фильтруем ТЕКУЩИЙ список
                val currentRecipes = _favoriteScreenState.value
                val filteredRecipes = currentRecipes.filter { recipe ->
                    recipe.id in favoriteIds
                }
                _favoriteScreenState.value = filteredRecipes
            }
        }
    }

    fun toggleFavorite(id: String) {
        repository.toggle(id)
    }

    private fun loadRecipes(){
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val favoriteIds = repository.favorites.value

                val allMeals: List<RecipePreview> = getRecipes(favoriteIds)
                    .mapNotNull { it.meals }
                    .flatten()
                    .map { converter.mealDataModelToPreview(it) }

                _favoriteScreenState.value = allMeals
                Log.d("HomeViewModel", "Recipes loaded: ${allMeals.size}")

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading recipes", e)
                _favoriteScreenState.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }


    // получение рецепта
    private suspend fun getRecipes(favoriteIds: Set<String>): List<MealsResponse> = try {
        val results = mutableListOf<Deferred<MealsResponse>>()

        for (recipeId in favoriteIds) {
            val deferred = viewModelScope.async(Dispatchers.IO) {
                FoodApi.retrofitService.getRecipeById(recipeId)
            }
            results.add(deferred)
        }

        results.awaitAll()

    } catch (e: Exception) {
        Log.e("HomeViewModel", "Error in getRecipes()", e)
        emptyList()
    }
}