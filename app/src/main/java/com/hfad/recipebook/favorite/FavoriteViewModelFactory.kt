package com.hfad.recipebook.favorite

import android.content.Context
import androidx.lifecycle.ViewModel
import com.hfad.recipebook.data.converters.DataConverter
import androidx.lifecycle.ViewModelProvider

class FavoriteViewModelFactory (
    private val context: Context,
    private val converter: DataConverter

) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {

            val repository = FavoritesRepository(context)

            @Suppress("UNCHECKED_CAST")
            return FavoriteViewModel(
                converter = converter,
                repository = repository
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}