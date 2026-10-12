package com.hfad.recipebook.di

import com.hfad.recipebook.data.remote.FoodService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Singleton


@Module // содержит правила создания зависимостей
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides // как создавать объект
    @Singleton
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true
        }

    // создаем Retrofit и передаём ему настройки JSON.
    // @Provides объясняет Hilt, как получить объект Retrofit
    @Provides
    @Singleton
    fun provideRetrofit(json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://www.themealdb.com/api/json/v1/1/")
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()

    // создаем реализацию FoodService на основе Retrofit
    // Hilt сможет передавать FoodService в конструкторы
    @Provides
    @Singleton
    fun provideFoodService(retrofit: Retrofit): FoodService =
        retrofit.create(FoodService::class.java)
}
