package com.hfad.recipebook

import com.hfad.recipebook.data.converter.DataConverter
import com.hfad.recipebook.data.remote.FoodService
import com.hfad.recipebook.data.remote.MealDataModel
import com.hfad.recipebook.data.remote.MealsResponse
import com.hfad.recipebook.domain.model.FilterType
import com.hfad.recipebook.domain.model.RecipePreview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import com.hfad.recipebook.ui.home.HomeViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    // FakeFoodService — поддельная реализация FoodService, не обращается к интернету и возвращает заданные ответы
    private class FakeFoodService : FoodService {
        var ingredientResponse = MealsResponse()
        var areaResponse = MealsResponse()
        var categoryResponse = MealsResponse()
        var recipeByIdResponse = MealsResponse()
        var lastCategory: String? = null
        var lastRecipeId: String? = null
        var lastArea: String? = null
        var lastIngredient: String? = null

        var searchError: Exception? = null
        var categoryError: Exception? = null
        var areaError: Exception? = null
        var ingredientError: Exception? = null
        var recipeByIdError: Exception? = null

        // Ответ, который возвращается при начальной загрузке
        var randomResponse = MealsResponse()

        // Ответ, который возвращается при поиске по названию
        var searchResponse = MealsResponse()

        // Запоминаем последний поисковый запрос, чтобы проверить, что ViewModel передала правильное значение
        var lastSearchQuery: String? = null

        override suspend fun getRandomMeal(): MealsResponse {
            return randomResponse
        }

        override suspend fun searchMealsByName(name: String): MealsResponse {
            lastSearchQuery = name
            // если задана ошибка, имитируем неудачный запрос к API
            searchError?.let { throw it }
            return searchResponse
        }

        override suspend fun filterByArea(area: String): MealsResponse {
            lastArea = area
            // если задана ошибка, имитируем неудачный запрос к API
            areaError?.let { throw it }
            return areaResponse
        }

        override suspend fun filterByCategory(category: String): MealsResponse {
            lastCategory = category
            // если задана ошибка, имитируем неудачный запрос к API
            categoryError?.let { throw it }
            return categoryResponse
        }

        override suspend fun filterByIngredients(ingredient: String): MealsResponse {
            lastIngredient = ingredient
            // если задана ошибка, имитируем неудачный запрос к API
            ingredientError?.let { throw it }
            return ingredientResponse
        }

        override suspend fun getRecipeById(id: String): MealsResponse {
            lastRecipeId = id
            recipeByIdError?.let { throw it }
            return recipeByIdResponse
        }
    }

    // диспетчер позволяет управлять выполнением корутин в тесте
    private val testDispatcher = StandardTestDispatcher()

    // перед каждым тестом подменяем Main на тестовый диспетчер
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    // после каждого теста восстанавливаем исходный Main
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun searchRecipes_success_updatesRecipeList() = runTest(testDispatcher) {
        val foodService = FakeFoodService()
        val meal = MealDataModel(
            idMeal = "52772",
            strMeal = "Teriyaki Chicken",
            strCategory = "Chicken",
            strArea = "Japanese",
            strMealThumb = "https://example.com/meal.jpg"
        )
        // при начальной загрузке ViewModel получит пустой ответ
        foodService.randomResponse = MealsResponse()

        // при поиске "Chicken" сервис вернет тестовый рецепт
        foodService.searchResponse = MealsResponse(meals = listOf(meal))

        val viewModel = HomeViewModel(
            converter = DataConverter(),
            foodService = foodService
        )

        // выполняем запланированные корутины начальной загрузки init
        advanceUntilIdle()
        // запускаем поиск рецептов
        viewModel.searchRecipes("Chicken")
        // ждем завершения корутины поиска
        advanceUntilIdle()

        // проверяем, что текст поиска сохранился
        assertEquals("Chicken", viewModel.searchQuery.value)
        // проверяем, что сервис получил правильный запрос.
        assertEquals("Chicken", foodService.lastSearchQuery)
        // проверяем, что API-ответ преобразовался в список RecipePreview
        assertEquals(
            listOf(
                RecipePreview(
                    id = "52772",
                    imageRes = "https://example.com/meal.jpg",
                    category = "Chicken",
                    title = "Teriyaki Chicken",
                    area = "Japanese",
                    quantity = 0
                )
            ),
            viewModel.homeScreenState.value
        )
        // после завершения поиска загрузка должна закончиться.
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun searchRecipes_emptyResponse_clearsRecipeList() = runTest(testDispatcher) {
        // подготавливаем сервис с пустым ответом
        val foodService = FakeFoodService()
        foodService.randomResponse = MealsResponse()
        foodService.searchResponse = MealsResponse(meals = emptyList())

        val viewModel = HomeViewModel(
            converter = DataConverter(),
            foodService = foodService
        )

        advanceUntilIdle()
        //  ищем рецепт, которого нет в ответе API
        viewModel.searchRecipes("UnknownRecipe")
        advanceUntilIdle()

        //  список рецептов должен быть пустым
        assertEquals(emptyList<RecipePreview>(), viewModel.homeScreenState.value)
        // поисковый запрос должен сохраниться
        assertEquals("UnknownRecipe", viewModel.searchQuery.value)
        assertFalse(viewModel.isLoading.value)
    }


    @Test
    fun searchRecipes_error_clearsRecipeListAndStopsLoading() = runTest(testDispatcher) {
        // настраиваем сервис так, чтобы поиск завершился ошибкой
        val foodService = FakeFoodService()
        foodService.randomResponse = MealsResponse()
        foodService.searchError = RuntimeException("Network error")

        val viewModel = HomeViewModel(
            converter = DataConverter(),
            foodService = foodService
        )

        advanceUntilIdle()
        // выполняем поиск, который должен завершиться ошибкой
        viewModel.searchRecipes("Chicken")
        advanceUntilIdle()

        //после ошибки список пуст
        assertEquals(emptyList<RecipePreview>(), viewModel.homeScreenState.value)
        // поисковый запрос должен сохраниться
        assertEquals("Chicken", viewModel.searchQuery.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun searchByFilter_category_success_updatesRecipeList() = runTest(testDispatcher) {

        val foodService = FakeFoodService()
        foodService.randomResponse = MealsResponse()

        val meal = MealDataModel(
            idMeal = "52772",
            strMeal = "Teriyaki Chicken",
            strMealThumb = "https://example.com/meal.jpg",
            strCategory = "Chicken",
            strArea = "Japanese"
        )

        // настраиваем ответ на запрос рецептов по категории
        // Сервис должен вернуть тестовый рецепт
        foodService.categoryResponse = MealsResponse(
            meals = listOf(meal)
        )

        // настраиваем ответ на запрос полной информации о рецепте по его id
        foodService.recipeByIdResponse = MealsResponse(
            meals = listOf(meal)
        )

        val viewModel = HomeViewModel(
            converter = DataConverter(),
            foodService = foodService
        )

        advanceUntilIdle()
        // имитируем выбор категории на экране приложения
        viewModel.searchByFilter("Chicken", FilterType.CATEGORY)
        advanceUntilIdle()

        // проверяем, что ViewModel запросила именно категорию Chicken
        assertEquals("Chicken", foodService.lastCategory)
        // проверяем, что ViewModel запросила полные данные рецепта с ожидаемым идентификатором
        assertEquals("52772", foodService.lastRecipeId)
        // проверяем, что в состоянии экрана появился один рецепт
        assertEquals(1, viewModel.homeScreenState.value.size)
        // проверяем, что название рецепта преобразовано правильно
        assertEquals("Teriyaki Chicken", viewModel.homeScreenState.value.first().title)
        // проверяем, что ViewModel сохранила выбранный фильтр как пару: тип фильтра и его значение
        assertEquals(FilterType.CATEGORY to "Chicken", viewModel.activeFilter.value)
        // проверяем, что после завершения поиска загрузка выключена
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun searchByFilter_category_error_clearsRecipeListAndStopsLoading() = runTest(testDispatcher) {

        val foodService = FakeFoodService()
        foodService.randomResponse = MealsResponse()
        foodService.categoryError = RuntimeException("Network error")

        val viewModel = HomeViewModel(
            converter = DataConverter(),
            foodService = foodService
        )

        advanceUntilIdle()
        viewModel.searchByFilter("Chicken", FilterType.CATEGORY)
        advanceUntilIdle()

        // после ошибки список пуст
        assertEquals(emptyList<RecipePreview>(), viewModel.homeScreenState.value)
        // фильтр должен не установиться
        assertEquals(null, viewModel.activeFilter.value)
        // состояние загрузки должно сброситься даже при ошибке
        assertFalse(viewModel.isLoading.value)

    }

    @Test
    fun searchByFilter_ingredient_success_updatesRecipeList() = runTest(testDispatcher) {

        val foodService = FakeFoodService()
        foodService.randomResponse = MealsResponse()

        val meal = MealDataModel(
            idMeal = "52772",
            strMeal = "Teriyaki Chicken",
            strMealThumb = "https://example.com/meal.jpg",
            strCategory = "Chicken",
            strArea = "Japanese",
            strIngredient1 = "Chicken",
        )

        foodService.ingredientResponse = MealsResponse(
            meals = listOf(meal)
        )
        foodService.recipeByIdResponse = MealsResponse(
            meals = listOf(meal)
        )

        val viewModel = HomeViewModel(
            converter = DataConverter(),
            foodService = foodService
        )

        advanceUntilIdle()
        viewModel.searchByFilter("Chicken", FilterType.INGREDIENT)
        advanceUntilIdle()

        assertEquals("Chicken", foodService.lastIngredient)
        assertEquals("52772", foodService.lastRecipeId)
        assertEquals(1, viewModel.homeScreenState.value.size)
        assertEquals("Teriyaki Chicken", viewModel.homeScreenState.value.first().title)
        assertEquals(FilterType.INGREDIENT to "Chicken", viewModel.activeFilter.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun searchByFilter_ingredient_error_clearsRecipeListAndStopsLoading() = runTest(testDispatcher) {

        val foodService = FakeFoodService()
        foodService.randomResponse = MealsResponse()
        foodService.ingredientError = RuntimeException("Network error")

        val viewModel = HomeViewModel(
            converter = DataConverter(),
            foodService = foodService
        )

        advanceUntilIdle()
        viewModel.searchByFilter("Chicken", FilterType.INGREDIENT)
        advanceUntilIdle()

        assertEquals(emptyList<RecipePreview>(), viewModel.homeScreenState.value)
        assertEquals(null, viewModel.activeFilter.value)
        assertFalse(viewModel.isLoading.value)

    }

    @Test
    fun searchByFilter_ingredient_errorLoadingRecipe_clearsRecipeListAndStopsLoading() = runTest(testDispatcher) {
        val foodService = FakeFoodService()
        foodService.randomResponse = MealsResponse()
        foodService.recipeByIdError = RuntimeException("Network error")

        val meal1 = MealDataModel(
            idMeal = "52772",
            strMeal = "Teriyaki Chicken",
            strMealThumb = "https://example.com/meal.jpg",
            strCategory = "Chicken",
            strArea = "Japanese",
            strIngredient1 = "Chicken",
        )

        val meal2 = MealDataModel(
            idMeal = "52773",
            strMeal = "Teriyaki",
            strMealThumb = "https://example.com/meal.jpg",
            strCategory = "Chicken",
            strArea = "Japanese",
            strIngredient1 = "Chicken",
        )

        foodService.ingredientResponse = MealsResponse(
            meals = listOf(meal1, meal2)
        )
        foodService.recipeByIdResponse = MealsResponse(
            meals = listOf(meal1, meal2)
        )

        val viewModel = HomeViewModel(
            converter = DataConverter(),
            foodService = foodService
        )

        advanceUntilIdle()
        viewModel.searchByFilter("Chicken", FilterType.INGREDIENT)
        advanceUntilIdle()

        assertEquals("Chicken", foodService.lastIngredient)
        assertEquals("52772", foodService.lastRecipeId)
        assertEquals(emptyList<RecipePreview>(), viewModel.homeScreenState.value)
        assertEquals(null, viewModel.activeFilter.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun searchByFilter_switchingFromSearch_clearsQueryAndSetsActiveFilter() = runTest(testDispatcher) {
        val foodService = FakeFoodService()
        foodService.randomResponse = MealsResponse()

        val viewModel = HomeViewModel(
            converter = DataConverter(),
            foodService = foodService
        )

        advanceUntilIdle()
        viewModel.searchRecipes("Chicken")
        advanceUntilIdle()

        // проверяем, что текст поиска сохранился
        assertEquals("Chicken", viewModel.searchQuery.value)
        // проверяем, что сервис получил правильный запрос.
        assertEquals("Chicken", foodService.lastSearchQuery)

        viewModel.searchByFilter("Japanese", FilterType.AREA)
        advanceUntilIdle()
        assertEquals("", viewModel.searchQuery.value)
        assertEquals(FilterType.AREA to "Japanese", viewModel.activeFilter.value)
        assertFalse(viewModel.isLoading.value)
    }

}

