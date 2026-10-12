package com.hfad.recipebook.ui.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.recipebook.data.converter.DataConverter
import com.hfad.recipebook.data.remote.FoodApi
import com.hfad.recipebook.data.remote.FoodService
import com.hfad.recipebook.data.repository.FavoritesRepository
import com.hfad.recipebook.domain.model.RecipeDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface RecipeDetailUiState {
    data object Loading: RecipeDetailUiState
    data class Recipe(
        val value: RecipeDetail,
        val youtubeVideoId: String?
    ) : RecipeDetailUiState

    data object Error: RecipeDetailUiState
}

class RecipeDetailViewModel(
    private val recipeId: String,
    private val converter: DataConverter,
    private val repository: FavoritesRepository,
    private val foodService: FoodService
): ViewModel() {

    private val _detailScreenState = MutableStateFlow<RecipeDetailUiState>(RecipeDetailUiState.Loading)
    val detailScreenState = _detailScreenState.asStateFlow()

    val isFavoriteState: StateFlow<Boolean> = repository.favoriteIds
        // combine() склеивает два разных потока данных в один
        // поток id из базы и поток с id текущего открытого рецепта.
        .combine(MutableStateFlow(recipeId)) { favoriteIds, id ->
            favoriteIds.contains(id)
        }
        // stateIn() превращает обычный холодный поток данных Flow в горячий StateFlow, чтобы мгновенно перерисовываться при изменениях
        .stateIn(
            // поток живет и работает, пока открыта эта ViewModel
            scope = viewModelScope,
            // если пользователь свернул экран через 5 сек отключится слежка за бд
            started = SharingStarted.WhileSubscribed(5000),
            // пока приложение делает самый первый запрос в бд рецепт считается не в избранном
            initialValue = false

        )

    init {
        loadRecipe(recipeId)
    }

    private fun loadRecipe(recipeId: String) {
        viewModelScope.launch {
            // 1. Сначала смотрим в базу: если рецепт в избранном, он лежит там целиком.
            //    Показываем его сразу, без запроса в сеть.
            val saved = repository.getFavorite(recipeId)
            if (saved != null) {
                _detailScreenState.value = RecipeDetailUiState.Recipe(
                    value = saved,
                    youtubeVideoId = extractYoutubeVideoId(saved.videoRes)
                )
                return@launch   // дальше в сеть не идём
            }

            // 2. В избранном рецепта нет: грузим из сети, как раньше
           _detailScreenState.value = RecipeDetailUiState.Loading
            try {
                val response = foodService.getRecipeById(recipeId)
                val meal = response.meals?.firstOrNull()

                if (meal != null) {
                    val recipeDetail = converter.mealDataModelToDetail(meal)
                    _detailScreenState.value = RecipeDetailUiState.Recipe(
                        value = recipeDetail,
                        youtubeVideoId = extractYoutubeVideoId(recipeDetail.videoRes)
                    )
                } else {
                    _detailScreenState.value = RecipeDetailUiState.Error
                }
            } catch (e: Exception) {
                println("FRIDGE detail error: $e")
                _detailScreenState.value = RecipeDetailUiState.Error
            }
        }
    }

    fun toggleFavorite(recipe: RecipeDetail) {
        viewModelScope.launch {
            repository.toggleFavorite(recipe)
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