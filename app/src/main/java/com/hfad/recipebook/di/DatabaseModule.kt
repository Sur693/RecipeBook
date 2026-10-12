package com.hfad.recipebook.di

import android.content.Context
import androidx.room.Room
import com.hfad.recipebook.data.local.AppDataBase
import com.hfad.recipebook.data.local.FavoriteDao
import com.hfad.recipebook.data.repository.FavoritesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module // содержит правила создания зависимостей
@InstallIn(SingletonComponent::class) // область доступности этих зависимостей
object DatabaseModule {

    @Provides // как создавать объект
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context // получение контекста приложения, не передавая его вручную
    ): AppDataBase {
        return Room.databaseBuilder(
            context,    // контекст приложения
            AppDataBase::class.java,    // класс нашей базы
            "fridge_db.db"              // имя файла базы на устройстве
        ).build()
    }

    // Получаем DAO из базы.
    // Hilt передаст этот объект в конструктор FavoritesRepository
    @Provides
    fun provideFavoriteDao(
        database: AppDataBase
    ): FavoriteDao = database.favoriteDao()
}
