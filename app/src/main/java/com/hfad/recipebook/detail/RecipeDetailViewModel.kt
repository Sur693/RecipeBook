package com.hfad.recipebook.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.recipebook.data.converters.DataConverter
import com.hfad.recipebook.data.remote.FoodApi
import com.hfad.recipebook.favorite.FavoritesRepository
import com.hfad.recipebook.model.RecipeDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface RecipeDetailUiState {
    data object Loading : RecipeDetailUiState
    data class Recipe(
        val value: RecipeDetail,
        val youtubeVideoId: String?
    ) : RecipeDetailUiState
}

class RecipeDetailViewModel(
    private val recipeId: String,
    private val converter: DataConverter,
    private val repository: FavoritesRepository
): ViewModel() {

    private val _detailScreenState = MutableStateFlow<RecipeDetailUiState>(RecipeDetailUiState.Loading)
    val detailScreenState = _detailScreenState.asStateFlow()

    val favorites = repository.favorites

    fun toggleFavorite() {
        repository.toggle(recipeId)
    }

    init {
        loadRecipe(recipeId)
    }

    private fun loadRecipe(recipeId: String) {
        viewModelScope.launch {
            _detailScreenState.value = RecipeDetailUiState.Loading  // показываем загрузку

            try {
                val response = FoodApi.retrofitService.getRecipeById(recipeId)
                val meal = response.meals?.firstOrNull()

                if (meal != null) {
                    val recipeDetail = converter.mealDataModelToDetail(meal)
                    val videoId = extractYoutubeVideoId(recipeDetail.videoRes)  // извлекаем ID

                    _detailScreenState.value = RecipeDetailUiState.Recipe(
                        value = recipeDetail,
                        youtubeVideoId = videoId
                    )
                } else {
                    Log.e("RecipeDetailViewModel", "Meal not found")
                }
            } catch (e: Exception) {
                Log.e("RecipeDetailViewModel", "Error loading recipe", e)
            }
        }
    }

    fun extractYoutubeVideoId(url: String?): String? {
        if (url.isNullOrEmpty()) return null

        val patterns = listOf(
            "(?<=watch\\?v=)[^&]+".toRegex(),
            "(?<=youtu.be/)[^?]+".toRegex()
        )

        for (pattern in patterns) {
            val match = pattern.find(url)
            if (match != null) {
                return match.value
            }
        }

        return null
    }
}