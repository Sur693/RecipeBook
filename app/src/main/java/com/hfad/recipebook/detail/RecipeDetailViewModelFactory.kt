package com.hfad.recipebook.detail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hfad.recipebook.data.converters.DataConverter
import com.hfad.recipebook.favorite.FavoritesRepository

class RecipeDetailViewModelFactory (
    private val context: Context,
    private val recipeId: String,
    private val converter: DataConverter
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecipeDetailViewModel(
                recipeId,
                converter,
                FavoritesRepository(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}