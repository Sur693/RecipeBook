package com.hfad.recipebook.data.converter


import com.hfad.recipebook.data.remote.MealDataModel
import com.hfad.recipebook.domain.model.RecipeDetail
import com.hfad.recipebook.domain.model.RecipePreview

class DataConverter {
    fun mealDataModelToDetail(model: MealDataModel): RecipeDetail =
        with(model){
            RecipeDetail(
                id = idMeal,
                imageRes = strMealThumb.orEmpty(),
                category = strCategory.orEmpty(),
                title = strMeal.orEmpty(),
                area = strArea.orEmpty(),
                ingredients = ingredients,
                measures = measures,
                videoRes = strYoutube.orEmpty(),
                instruction = strInstructions.orEmpty(),
                quantity = ingredients.size
            )
        }


    fun mealDataModelToPreview(model: MealDataModel): RecipePreview =
        with(model){
            RecipePreview(
                id = idMeal,
                imageRes = strMealThumb,
                category = strCategory.orEmpty(),
                title = strMeal.orEmpty(),
                area = strArea.orEmpty(),
                quantity = ingredients.size
            )
        }
}