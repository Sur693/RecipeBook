package com.hfad.recipebook.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    // flow - при любом изменении таблицы автоматически обновляет список
    @Query("SELECT * FROM favorites")
    fun observeAll(): Flow<List<FavoriteEntity>>

    // Только id, чтобы закрашивать сердечко
    @Query("SELECT id FROM favorites")
    fun observeIds(): Flow<List<String>>

    // Upsert - обновить, если такой id уже есть, иначе добавить
    @Upsert
    suspend fun upsert(entity: FavoriteEntity)

    // :id в запросе подставляется из параметра функции
    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM favorites WHERE id = :id")
    suspend fun getById(id: String): FavoriteEntity?
}