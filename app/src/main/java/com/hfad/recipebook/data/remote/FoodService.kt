package com.hfad.recipebook.data.remote

import com.hfad.recipebook.data.remote.MealsResponse
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query


private const val BASE_URL = "www.themealdb.com/api/json/v1/1/"

val json = Json {ignoreUnknownKeys = true}
private val retrofit = Retrofit.Builder()
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

internal object FoodApi{
    val retrofitService: FoodService by lazy{
        retrofit.create(FoodService::class.java)
    }
}

internal interface FoodService{

    @GET("random.php")
    suspend fun getRandomMeal(): MealsResponse

    @GET("lookup.php")  // value = можно убрать, это параметр по умолчанию
    suspend fun getRecipeById(@Query("i") id: String): MealsResponse
}