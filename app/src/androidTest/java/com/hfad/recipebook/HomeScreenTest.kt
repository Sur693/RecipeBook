package com.hfad.recipebook

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import org.junit.Rule
import org.junit.Test
import com.hfad.recipebook.data.converter.DataConverter
import com.hfad.recipebook.data.remote.FoodService
import com.hfad.recipebook.data.remote.MealDataModel
import com.hfad.recipebook.data.remote.MealsResponse
import com.hfad.recipebook.domain.model.FilterType
import com.hfad.recipebook.ui.home.HomeScreen
import com.hfad.recipebook.ui.home.HomeViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.CompletableDeferred

class HomeScreenTest {

    // запускает Compose-интерфейс в тестовом окружении
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_displaysMainElements() {
        val foodService = FakeFoodService()

        val viewModel = HomeViewModel(
            converter = DataConverter(),
            foodService = foodService
        )

        // отображаем HomeScreen в тестовом окружении
        composeTestRule.setContent {
            HomeScreen(
                viewModel = viewModel
            )
        }

        // проверяем, что название приложения отображается на экране
        composeTestRule
            .onNodeWithText("Fridge")  // найти элемент содержащий ...
            .assertIsDisplayed() // проверить, что найденный элемент отображается

        // проверяем, что отображается поле поиска с подсказкой
        composeTestRule
            .onNodeWithText("Search recipes")
            .assertIsDisplayed()

        // проверяем заголовок списка рецептов
        composeTestRule
            .onNodeWithText("Recipes you can make")
            .assertIsDisplayed()

        // проверяем кнопку фильтров по её contentDescription
        composeTestRule
            .onNodeWithContentDescription("Filter") // найти элемент по описанию
            .assertIsDisplayed()

        // проверяем кнопку избранного
        composeTestRule
            .onNodeWithContentDescription("Favorites")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_searchField_acceptsText() {
        val foodService = FakeFoodService()

        val viewModel = HomeViewModel(
            converter = DataConverter(),
            foodService = foodService
        )

        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel)
        }

        // находим поле поиска по его подсказке и вводим текст
        composeTestRule
            .onNodeWithText("Search recipes")
            .performTextInput("Chicken")

        // проверяем, что введённый текст отображается в поле.
        composeTestRule
            .onNodeWithText("Chicken")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_clearSearchButton_clearsText() {
        val foodService = FakeFoodService()

        val viewModel = HomeViewModel(
            converter = DataConverter(),
            foodService = foodService
        )

        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel)
        }

        // находим поле поиска и вводим название рецепта
        composeTestRule
            .onNodeWithText("Search recipes")
            .performTextInput("Chicken") // имитирует ввод текста в текстовое поле

        // проверяем, что текст появился
        composeTestRule
            .onNodeWithText("Chicken")
            .assertIsDisplayed()

        // нажимаем на кнопку очистки текста
        composeTestRule
            .onNodeWithContentDescription("Clear Text")
            .performClick() // имитирует нажатие

        // проверяем, что введенный текст больше не отображается
        composeTestRule
            .onNodeWithText("Chicken")
            .assertDoesNotExist() // проверить, что элемент не отображается
    }

    @Test
    fun homeScreen_search_callsFoodService() {
        val foodService = FakeFoodService()

        val viewModel = HomeViewModel(
            converter = DataConverter(),
            foodService = foodService
        )

        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel)
        }

        // вводим запрос в поисковое поле
        composeTestRule
            .onNodeWithText("Search recipes")
            .performTextInput("Chicken")

        // проверяем, что сервис получил именно этот запрос
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            foodService.lastSearchQuery == "Chicken"
        }

    }

    @Test
    fun homeScreen_search_replacesPreviousQuery() {
        val chicken = MealDataModel(
            idMeal = "1",
            strMeal = "Chicken Recipe",
            strCategory = "Chicken",
            strArea = "American",
            strMealThumb = "https://example.com/chicken.jpg",
            strIngredient1 = "Chicken",
            strIngredient2 = "Salt",
            strMeasure1 = "500 g",
            strMeasure2 = "1 tsp"
        )

        val pasta = MealDataModel(
            idMeal = "2",
            strMeal = "Pasta Recipe",
            strCategory = "Pasta",
            strArea = "Italian",
            strMealThumb = "https://example.com/pasta.jpg",
            strIngredient1 = "Pasta",
            strIngredient2 = "Tomato",
            strMeasure1 = "200 g",
            strMeasure2 = "100 g"
        )

        val foodService = FakeFoodService(
            responsesByQuery = mapOf(
                "Chicken" to MealsResponse(meals = listOf(chicken)),
                "Pasta" to MealsResponse(meals = listOf(pasta))
            )
        )

        val viewModel = HomeViewModel(
            converter = DataConverter(),
            foodService = foodService
        )

        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel)
        }

        composeTestRule
            .onNodeWithText("Search recipes")
            .performTextInput("Chicken")

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            foodService.lastSearchQuery == "Chicken"
        }

        foodService.finishSearch.complete(Unit)

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            viewModel.homeScreenState.value.any {
                it.title == "Chicken Recipe"
            }
        }

        composeTestRule
            .onNodeWithText("Chicken Recipe")
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasSetTextAction() and hasText("Chicken")
            )
            .performTextReplacement("Pasta")

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            foodService.lastSearchQuery == "Pasta"
        }

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            viewModel.homeScreenState.value.any {
                it.title == "Pasta Recipe"
            }
        }

        composeTestRule
            .onNodeWithText("Pasta Recipe")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Chicken Recipe")
            .assertDoesNotExist()
    }

    @Test
    fun homeScreen_search_clearsPreviousQueryInService() {
        val foodService = FakeFoodService()

        val viewModel = HomeViewModel(
            converter = DataConverter(),
            foodService = foodService
        )

        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel)
        }

        // вводим запрос в поисковое поле
        composeTestRule
            .onNodeWithText("Search recipes")
            .performTextInput("Chicken")

        // нажимаем на кнопку очистки текста
        composeTestRule
            .onNodeWithContentDescription("Clear Text")
            .performClick() // имитирует нажатие


        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            foodService.lastSearchQuery == ""
        }
    }

    @Test
    fun homeScreen_search_clearsActiveFilter() {
        val foodService = FakeFoodService()

        val viewModel = HomeViewModel(
            converter = DataConverter(),
            foodService = foodService
        )

        // устанавливаем фильтр до отображения экрана
        viewModel.searchByFilter("Seafood", FilterType.CATEGORY)

        // отображаем экран
        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel)
        }

        // проверяем, что фильтр отображается
        composeTestRule
            .onNodeWithText("Filter: Seafood")
            .assertIsDisplayed()

        // вводим поисковый запрос
        composeTestRule
            .onNodeWithText("Search recipes")
            .performTextInput("Chicken")

        // ждем, пока сервис получит запрос
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            foodService.lastSearchQuery == "Chicken"
        }

        // проверяем, что фильтр исчез после начала поиска
        composeTestRule
            .onNodeWithText("Filter: Seafood")
            .assertDoesNotExist()
    }

    @Test
    fun homeScreen_search_showsLoadingAndStopsAfterError() {
        val foodService = FakeFoodService(
            shouldThrowError = true
        )

        val viewModel = HomeViewModel(
            converter = DataConverter(),
            foodService = foodService
        )

        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel)
        }

        composeTestRule
            .onNodeWithText("Search recipes")
            .performTextInput("Chicken")

        // ждем, пока сервис получит запрос и остановится в ожидании.
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            foodService.searchStarted.isCompleted
        }

        // пока сервис не завершил поиск, индикатор должен отображаться.
        composeTestRule
            .onNodeWithTag("loading_indicator")
            .assertIsDisplayed()

        // разрешаем сервису продолжить работу и выбросить ошибку
        foodService.finishSearch.complete(Unit)

        // ждем, пока ViewModel закончит обработку ошибки
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            !viewModel.isLoading.value
        }

        // после ошибки индикатор должен исчезнуть
        composeTestRule
            .onNodeWithTag("loading_indicator")
            .assertDoesNotExist()

        // экран остается доступным
        composeTestRule
            .onNodeWithText("Fridge")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_search_displaysRecipes() {
        val testMeal = MealDataModel(
            idMeal = "52772",
            strMeal = "Teriyaki Chicken",
            strCategory = "Chicken",
            strArea = "Japanese",
            strMealThumb = "https://example.com/meal.jpg",
            strIngredient1 = "Chicken",
            strIngredient2 = "Soy sauce",
            strMeasure1 = "500 g",
            strMeasure2 = "2 tbsp"
        )

        val foodService = FakeFoodService(
            searchResponse = MealsResponse(
                meals = listOf(testMeal)
            )
        )

        val viewModel = HomeViewModel(
            converter = DataConverter(),
            foodService = foodService
        )

        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel)
        }

        composeTestRule
            .onNodeWithText("Teriyaki Chicken")
            .assertDoesNotExist()


        composeTestRule
            .onNodeWithText("Search recipes")
            .performTextInput("Teriyaki")

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            foodService.lastSearchQuery != null
        }

        assertEquals("Teriyaki", foodService.lastSearchQuery)

        foodService.finishSearch.complete(Unit)

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            !viewModel.isLoading.value
        }

        composeTestRule
            .onNodeWithText("Teriyaki Chicken")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_clickRecipe_navigatesToDetail() {
        val testMeal = MealDataModel(
            idMeal = "52772",
            strMeal = "Teriyaki Chicken",
            strCategory = "Chicken",
            strArea = "Japanese",
            strMealThumb = "https://example.com/meal.jpg",
            strIngredient1 = "Chicken",
            strIngredient2 = "Soy sauce",
            strMeasure1 = "500 g",
            strMeasure2 = "2 tbsp"
        )

        val foodService = FakeFoodService(
            searchResponse = MealsResponse(
                meals = listOf(testMeal)
            )
        )

        val viewModel = HomeViewModel(
            converter = DataConverter(),
            foodService = foodService
        )

        var navigatedRecipeId: String? = null

        composeTestRule.setContent {
            HomeScreen(
                viewModel = viewModel,
                navigateToDetail = { id ->
                    navigatedRecipeId = id
                }
            )
        }

        composeTestRule
            .onNodeWithText("Search recipes")
            .performTextInput("Teriyaki")

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            foodService.lastSearchQuery == "Teriyaki"
        }

        foodService.finishSearch.complete(Unit)

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            !viewModel.isLoading.value
        }

        composeTestRule
            .onNodeWithText("Teriyaki Chicken")
            .performClick()

        assertEquals("52772", navigatedRecipeId)
    }

    @Test
    fun homeScreen_filterButton_opensFilterScreen() {
        var filterScreenOpened = false

        val foodService = FakeFoodService()

        val viewModel = HomeViewModel(
            converter = DataConverter(),
            foodService = foodService
        )

        composeTestRule.setContent {
            HomeScreen(
                navigateToFilter = {
                    filterScreenOpened = true
                },
                viewModel = viewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Filter")
            .performClick()

        assertTrue(filterScreenOpened)
    }

    @Test
    fun homeScreen_favoritesButton_opensFavoritesScreen() {
        var favoritesScreenOpened = false

        val foodService = FakeFoodService()

        val viewModel = HomeViewModel(
            converter = DataConverter(),
            foodService = foodService
        )

        composeTestRule.setContent {
            HomeScreen(
                navigateToFavorite = {
                    favoritesScreenOpened = true
                },
                viewModel = viewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Favorites")
            .performClick()

        assertTrue(favoritesScreenOpened)
    }
}

private class FakeFoodService(
    private val shouldThrowError: Boolean = false,
    private val searchResponse: MealsResponse = MealsResponse(),
    private val responsesByQuery: Map<String, MealsResponse> = emptyMap()
) : FoodService {

    var lastSearchQuery: String? = null
        private set

    // сообщает тесту, что поиск действительно начался
    val searchStarted = CompletableDeferred<Unit>()
    // не дает поиску завершиться, пока тест сам этого не разрешит
    val finishSearch = CompletableDeferred<Unit>()

    override suspend fun getRandomMeal() = MealsResponse()

    override suspend fun getRecipeById(id: String) = MealsResponse()

    override suspend fun searchMealsByName(name: String): MealsResponse {

        lastSearchQuery = name

        searchStarted.complete(Unit)

        finishSearch.await()

        if (shouldThrowError) {
            throw RuntimeException("Test search error")
        }

        return responsesByQuery[name] ?: searchResponse
    }

    override suspend fun filterByCategory(category: String): MealsResponse {
        return MealsResponse()
    }

    override suspend fun filterByArea(area: String): MealsResponse {
        return MealsResponse()
    }

    override suspend fun filterByIngredients(ingredient: String): MealsResponse {
        return MealsResponse()
    }
}