package com.hfad.recipebook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hfad.recipebook.ui.navigation.AppNavigation
import com.hfad.recipebook.ui.theme.RecipeBookTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // для внедрения зависимостей с помощью hilt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecipeBookTheme {
                AppNavigation()
            }
        }
    }
}
