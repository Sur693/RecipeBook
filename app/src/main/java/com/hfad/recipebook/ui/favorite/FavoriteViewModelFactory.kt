package com.hfad.recipebook.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hfad.recipebook.data.repository.FavoritesRepository

class FavoriteViewModelFactory(
    // репозиторий приходит снаружи, его создаёт RecipeBookApp (одна база на всё приложение)
    private val repository: FavoritesRepository
) : ViewModelProvider.Factory {

    // Система вызывает этот метод, когда ей нужна ViewModel
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Проверяем, что просят именно FavoriteViewModel, а не какую-то другую
        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            return FavoriteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}