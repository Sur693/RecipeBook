package com.hfad.recipebook.favorite

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import com.hfad.recipebook.data.FavoritesKeys
import com.hfad.recipebook.data.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FavoritesRepository(
    private val context: Context
) {
    private val prefs = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)

    private val _favorites = MutableStateFlow(load())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()  // ← Публичный StateFlow

    private fun load(): Set<String> {
        val result = prefs.getStringSet("ids", emptySet()) ?: emptySet()
        Log.d("FavoritesRepository", "load(): $result")
        return result
    }

    private fun save(set: Set<String>) {
        prefs.edit().putStringSet("ids", set).apply()
        Log.d("FavoritesRepository", "save(): $set")
    }

    fun toggle(id: String) {
        Log.d("FavoritesRepository", "toggle() called for id: $id")
        Log.d("FavoritesRepository", "Before toggle: ${_favorites.value}")

        val newSet = _favorites.value.toMutableSet().apply {
            if (contains(id)) {
                remove(id)
                Log.d("FavoritesRepository", "Removed $id")
            } else {
                add(id)
                Log.d("FavoritesRepository", "Added $id")
            }
        }

        save(newSet)
        _favorites.value = newSet

        Log.d("FavoritesRepository", "After toggle: ${_favorites.value}")
    }
}
