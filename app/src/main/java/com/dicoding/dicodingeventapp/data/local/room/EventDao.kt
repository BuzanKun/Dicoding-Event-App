package com.dicoding.dicodingeventapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dicoding.dicodingeventapp.data.local.entity.EventEntity

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEvent(event: List<EventEntity>)

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Query("DELETE FROM event WHERE status = :status")
    suspend fun deleteEventsByStatus(status: Int)

    @Query("SELECT * FROM event WHERE id = :id")
    fun getEventById(id: Int): LiveData<EventEntity>

    @Query("SELECT * FROM event WHERE status = :status ORDER BY beginTime ASC")
    fun getEventByStatus(status: Int): LiveData<List<EventEntity>>

    @Query("SELECT * FROM event WHERE name LIKE '%' || :query || '%'")
    fun searchEvents(query: String): LiveData<List<EventEntity>>

    @Query("SELECT * FROM event WHERE favorited = 1")
    fun getFavoriteEvent(): LiveData<List<EventEntity>>

    @Query("SELECT COUNT(*) FROM event WHERE id = :id AND favorited = 1")
    suspend fun isFavoriteEvent(id: Int?): Boolean
}