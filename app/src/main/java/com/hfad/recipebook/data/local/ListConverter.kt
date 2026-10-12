package com.hfad.recipebook.data.local

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

class ListConverter {
    // список в строку
    @TypeConverter
    fun fromList(list: List<String>): String = Json.encodeToString(list)

    // строку в список
    @TypeConverter
    fun toList(value: String): List<String> = Json.decodeFromString(value)
}