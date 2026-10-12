package com.hfad.recipebook.data.repository


import com.hfad.recipebook.data.local.FavoriteDao
import com.hfad.recipebook.data.local.toDomain
import com.hfad.recipebook.data.local.toEntity
import com.hfad.recipebook.domain.model.RecipeDetail
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

// Класс репозитория, который принимает инструмент запросов к базе (FavoriteDao)
@Singleton
class FavoritesRepository @Inject constructor( // создание репозитория через конструктор
    private val favoriteDao: FavoriteDao
) {
    // получаем из базы список строк, конвертируем в модели приложения и отдаем наружу
    val favorites: Flow<List<RecipeDetail>> = favoriteDao.observeAll()
        .map { entities -> entities.map { it.toDomain() } }

    // получаем поток, содержащий только id избранных рецептов (для покраски сердечек)
    val favoriteIds: Flow<List<String>> = favoriteDao.observeIds()

    // переключения статуса: добавляет рецепт в избранное или удаляет его
    suspend fun toggleFavorite(recipe: RecipeDetail) {
        // текущий список сохраненных id из базы данных
        val currentIds = favoriteDao.observeIds().firstOrNull() ?: emptyList()

        if (currentIds.contains(recipe.id)) {
            favoriteDao.deleteById(recipe.id)
        } else {
            favoriteDao.upsert(recipe.toEntity())
        }
    }

    suspend fun getFavorite(id: String): RecipeDetail? = favoriteDao.getById(id)?.toDomain()
}