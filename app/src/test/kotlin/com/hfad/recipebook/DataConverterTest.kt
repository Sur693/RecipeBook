package com.hfad.recipebook

import com.hfad.recipebook.data.converter.DataConverter
import com.hfad.recipebook.data.remote.MealDataModel
import junit.framework.TestCase.assertEquals
import org.junit.Test

class DataConverterTest {

    // создаем объект, который будем проверять
    private val converter = DataConverter()

    @Test
    fun mealDataModelToDetail_mapsRecipeFields() {
        // Arrange — подготавливаем входные данные
        val meal = MealDataModel(
            idMeal = "52772",
            strMeal = "Teriyaki Chicken",
            strCategory = "Chicken",
            strArea = "Japanese",
            strInstructions = "Mix the ingredients and cook.",
            strMealThumb = "https://example.com/meal.jpg",
            strYoutube = "https://youtube.com/watch?v=example",
            strIngredient1 = "Chicken",
            strIngredient2 = "Soy sauce",
            strMeasure1 = "500 g",
            strMeasure2 = "2 tbsp"
        )

        // Act — вызываем настоящий метод DataConverter
        val result = converter.mealDataModelToDetail(meal)

        // Assert — проверяем результат
        assertEquals("52772", result.id)
        assertEquals("Teriyaki Chicken", result.title)
        assertEquals("Chicken", result.category)
        assertEquals("Japanese", result.area)
        assertEquals("https://example.com/meal.jpg", result.imageRes)
        assertEquals("https://youtube.com/watch?v=example", result.videoRes)
        assertEquals("Mix the ingredients and cook.", result.instruction)
        assertEquals(listOf("Chicken", "Soy sauce"), result.ingredients)
        assertEquals(listOf("500 g", "2 tbsp"), result.measures)
        assertEquals(2, result.quantity)
    }


    @Test
    fun mealDataModelToDetail_nullOptionalFields_becomeEmptyStrings() {
        val meal = MealDataModel(
            idMeal = "123"
        )

        val result = converter.mealDataModelToDetail(meal)

        assertEquals("", result.title)
        assertEquals("", result.category)
        assertEquals("", result.area)
        assertEquals("", result.imageRes)
        assertEquals("", result.videoRes)
        assertEquals("", result.instruction)
        assertEquals(emptyList<String>(), result.ingredients)
        assertEquals(emptyList<String>(), result.measures)
        assertEquals(0, result.quantity)
    }

    @Test
    fun mealDataModelToPreview_mapsRecipeFields() {
        // Arrange — подготавливаем входные данные
        val meal = MealDataModel(
            idMeal = "52772",
            strMeal = "Teriyaki Chicken",
            strCategory = "Chicken",
            strArea = "Japanese",
            strMealThumb = "https://example.com/meal.jpg",
            strIngredient1 = "Chicken",
            strIngredient2 = "Soy sauce"
        )

        // Act — вызываем настоящий метод DataConverter
        val result = converter.mealDataModelToPreview(meal)

        // Assert — проверяем результат
        assertEquals("52772", result.id)
        assertEquals("Teriyaki Chicken", result.title)
        assertEquals("Chicken", result.category)
        assertEquals("Japanese", result.area)
        assertEquals("https://example.com/meal.jpg", result.imageRes)
        assertEquals(2, result.quantity)
    }


    @Test
    fun mealDataModelToPreview_nullOptionalFields_becomeEmptyStrings() {
        val meal = MealDataModel(
            idMeal = "123"
        )

        val result = converter.mealDataModelToPreview(meal)

        assertEquals("123", result.id)
        assertEquals("", result.title)
        assertEquals("", result.category)
        assertEquals("", result.area)
        assertEquals(null, result.imageRes)
        assertEquals(0, result.quantity)
    }

}