package com.hfad.recipebook.model

import android.icu.util.Measure
import org.w3c.dom.ProcessingInstruction

class RecipeDetail (
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