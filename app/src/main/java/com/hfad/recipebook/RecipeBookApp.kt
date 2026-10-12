package com.hfad.recipebook

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// включает Hilt для всего приложения
// с этого класса начинается настройка графа зависимостей.
@HiltAndroidApp
class RecipeBookApp : Application()