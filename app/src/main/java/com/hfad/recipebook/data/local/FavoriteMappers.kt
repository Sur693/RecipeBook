package com.hfad.recipebook.data.local

import com.hfad.recipebook.domain.model.RecipeDetail

fun RecipeDetail.toEntity(): FavoriteEntity {
    return FavoriteEntity(
        id = id,
        imageRes = imageRes,
        category = category,
        title = title,
        area = area,
        ingredients = ingredients,
        measures = measures,
        videoRes = videoRes,
        instruction = instruction,
        quantity = quantity
    )
}

fun FavoriteEntity.toDomain(): RecipeDetail{
    return RecipeDetail(
        id = id,
        imageRes = imageRes,
        category = category,
        title = title,
        area = area,
        ingredients = ingredients,
        measures = measures,
        videoRes = videoRes,
        instruction = instruction,
        quantity = quantity
    )
}