package com.hfad.recipebook.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

// настройка бд
@Database(entities = [FavoriteEntity::class], version = 1, exportSchema = false)
// подключение конвертер для хранения списков
@TypeConverters(ListConverter::class)

abstract class AppDataBase : RoomDatabase() {
    // доступ к запросам базы данных
    abstract fun favoriteDao(): FavoriteDao
}
