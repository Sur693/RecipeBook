package com.hfad.recipebook.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hfad.recipebook.data.converter.DataConverter
import com.hfad.recipebook.data.remote.FoodApi
import com.hfad.recipebook.data.repository.FavoritesRepository

class RecipeDetailViewModelFactory(
    private val recipeId: String,
    private val repository: FavoritesRepository   // берётся из RecipeBookApp
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Собираем ViewModel и подставляем все четыре зависимости
        return RecipeDetailViewModel(
            recipeId = recipeId,
            converter = DataConverter(),
            repository = repository,
            foodService = FoodApi.retrofitService
        ) as T
    }
}