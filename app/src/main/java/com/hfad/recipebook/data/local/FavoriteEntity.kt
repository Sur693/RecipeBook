package com.hfad.recipebook.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    // уникальный ключ
    @PrimaryKey val id: String,
    val imageRes: String,
    val category: String,
    val title: String,
    val area: String,
    val ingredients: List<String>,
    val measures: List<String>,
    val videoRes: String,
    val instruction: String,
    val quantity: Int
)
