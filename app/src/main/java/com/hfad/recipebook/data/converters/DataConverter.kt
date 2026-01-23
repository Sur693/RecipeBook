package com.hfad.recipebook.data.converters

import android.util.Log
import com.hfad.recipebook.data.locale.RecipeEntity
import com.hfad.recipebook.data.remote.MealDataModel
import com.hfad.recipebook.model.RecipeDetail
import com.hfad.recipebook.model.RecipePreview

class DataConverter(

) {
    fun mealDataModelToDetail(model: MealDataModel): RecipeDetail =
        with(model){
            RecipeDetail(
                id = this.idMeal,
                imageRes = this.strImageSource.orEmpty(),
                category = this.strCategory.orEmpty(),
                title = this.strMeal.orEmpty(),
                ingredients = emptyList()
            )
        }


    fun recipeDetailToEntity(detail: RecipeDetail): RecipeEntity =
        with(detail){
            RecipeEntity(
                idMeal = this.id,
                strMeal = this.title,
                strCategory = this.category,
                strImageSource = this.imageRes
            )
        }

    fun recipeEntityToDetail(entity: RecipeEntity): RecipeDetail =
        with(entity){
            RecipeDetail(
            id = this.idMeal,
            imageRes = strImageSource.orEmpty(),
            category = strCategory.orEmpty(),
            title = strMeal.orEmpty(),
            ingredients = emptyList()
        )
    }

    fun mealDataModelToPreview(model: MealDataModel): RecipePreview =
        with(model){
            Log.d("MealDataModel", "Image URL: ${strMealThumb.orEmpty()}")
            RecipePreview(
                id = idMeal,
                imageRes = strMealThumb,
                category = strCategory.orEmpty(),
                title = strMeal.orEmpty()
            )
        }
}