package com.hfad.recipebook.data.remote


import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

private val json = Json { ignoreUnknownKeys = true }
val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType())) // используем сериализацию
    .build()

internal object FoodApi {
    val retrofitService: FoodService by lazy {
        retrofit.create(FoodService::class.java)
    }
}

internal interface FoodService{

    @GET("random.php")
    suspend fun getRandomMeal(): MealsResponse

    @GET("lookup.php") // по id
    suspend fun getRecipeById(@Query("i") id: String): MealsResponse

    @GET("search.php") // по названию
    suspend fun searchMealsByName(@Query("s") query: String): MealsResponse

    }