package com.hfad.recipebook.data

import android.content.Context
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(name = "favorites")

object FavoritesKeys {
    val FAVORITES = stringSetPreferencesKey("favorite_ids")
}