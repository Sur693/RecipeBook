package com.hfad.recipebook

import android.app.Application
import androidx.room.Room
import com.hfad.recipebook.data.local.AppDatabase
import com.hfad.recipebook.data.repository.FavoritesRepository

class RecipeBookApp : Application() {

    // база создается один раз при первом обращении и дальше переиспользуется
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            this,                       // контекст приложения
            AppDatabase::class.java,    // класс нашей базы
            "fridge_db.db"              // имя файла базы на устройстве
        ).build()
    }

    // репозиторий получает Dao из базы
    val favoritesRepository: FavoritesRepository by lazy {
        FavoritesRepository(database.favoriteDao())
    }
}