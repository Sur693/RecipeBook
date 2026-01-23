package com.hfad.recipebook.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hfad.recipebook.data.converters.DataConverter

class HomeViewModelFactory(
    private val converter: DataConverter
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Проверяем, что модель это HomeViewModel
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(converter) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}