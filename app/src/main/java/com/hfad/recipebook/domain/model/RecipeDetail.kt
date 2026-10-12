package com.hfad.recipebook.domain.model


data class RecipeDetail (
    val id: String,
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