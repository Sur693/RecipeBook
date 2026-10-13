package com.hfad.recipebook

import com.hfad.recipebook.data.local.FavoriteDao
import com.hfad.recipebook.data.local.FavoriteEntity
import com.hfad.recipebook.data.local.toEntity
import com.hfad.recipebook.data.repository.FavoritesRepository
import com.hfad.recipebook.domain.model.RecipeDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Test


class FavoritesRepositoryTest {
    private val recipe = RecipeDetail(
        id = "52772",
        imageRes = "https://example.com/meal.jpg",
        category = "Chicken",
        title = "Teriyaki Chicken",
        area = "Japanese",
        ingredients = listOf("Chicken", "Soy sauce"),
        measures = listOf("500 g", "2 tbsp"),
        videoRes = "https://youtube.com/watch?v=example",
        instruction = "Mix the ingredients and cook.",
        quantity = 2
    )

    // FakeFavoriteDao — поддельная реализация FavoriteDao
    private class FakeFavoriteDao : FavoriteDao {

        // Вместо настоящей базы Room используем обычную коллекцию
        // ключ — id рецепта, значение — объект FavoriteEntity
        private val entities = mutableMapOf<String, FavoriteEntity>()

        // flowOf() создаёт Flow, который выдаёт переданный список
        override fun observeAll(): Flow<List<FavoriteEntity>> =
            flowOf(entities.values.toList())

        override fun observeIds(): Flow<List<String>> =
            flowOf(entities.keys.toList())

        // добавление рецепта или обновление существующего
        override suspend fun upsert(entity: FavoriteEntity) {
            entities[entity.id] = entity
        }

        // удаление рецепта по id.
        override suspend fun deleteById(id: String) {
            entities.remove(id)
        }

        // получение одного рецепта по id
        override suspend fun getById(id: String): FavoriteEntity? =
            entities[id]
    }

    @Test
    fun toggleFavorite_recipeNotSaved_savesRecipe() = runBlocking {

        val dao = FakeFavoriteDao()
        val repository = FavoritesRepository(dao)

        repository.toggleFavorite(recipe)

        // проверяем, что запись появилась в dao
        assertNotNull(dao.getById(recipe.id))

        // проверяем, что сохранен рецепт с нужным id
        assertEquals(recipe.id, dao.getById(recipe.id)?.id)
    }

    @Test
    fun toggleFavorite_recipeAlreadySaved_removesRecipe() = runBlocking {

        val dao = FakeFavoriteDao()
        val repository = FavoritesRepository(dao)

        // сохраняем рецепт, чтобы его можно было удалить
        dao.upsert(recipe.toEntity())

        // репозиторий должен увидеть существующий id и удалить запись.
        repository.toggleFavorite(recipe)

        // после удаления запись должна отсутствовать.
        assertNull(dao.getById(recipe.id))
    }

    @Test
    fun getFavorite_recipeExists_returnsRecipe() = runBlocking {

        val dao = FakeFavoriteDao()
        val repository = FavoritesRepository(dao)

        // сохраняем рецепт, чтобы его можно было получить
        dao.upsert(recipe.toEntity())

        // запрашиваем рецепт по его id.
        val result = repository.getFavorite(recipe.id)

        assertEquals(recipe, result)
    }

    @Test
    fun getFavorite_recipeDoesNotExist_returnsNull() = runBlocking {

        val dao = FakeFavoriteDao()
        val repository = FavoritesRepository(dao)

        // ищем id, которого нет в базе
        val result = repository.getFavorite("unknown-id")

        // ожидаем отсутствие рецепта
        assertNull(result)
    }
}
