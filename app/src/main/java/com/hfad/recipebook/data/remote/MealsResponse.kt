package com.hfad.recipebook.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class MealsResponse (
    val meals: List<MealDataModel>? = null
)