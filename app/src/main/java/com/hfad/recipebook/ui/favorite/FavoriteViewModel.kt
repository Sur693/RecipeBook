package com.hfad.recipebook.ui.favorite


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.recipebook.data.repository.FavoritesRepository
import com.hfad.recipebook.domain.model.RecipeDetail
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class FavoriteViewModel(
    repository: FavoritesRepository
) : ViewModel() {

    // поток экрана берет живой поток избранных рецептов из репозитория
    // stateIn - поток будет активен только тогда, когда пользователь смотрит на экран
    val favoriteScreenState: StateFlow<List<RecipeDetail>> = repository.favorites
        .stateIn(
            scope = viewModelScope,
            // закрываем подписку через 5 секунд после ухода с экрана
            started = SharingStarted.WhileSubscribed(5000),
            // начальное состояние, пока база данных загружается
            initialValue = emptyList()
        )
}