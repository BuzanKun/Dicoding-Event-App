package com.dicoding.dicodingeventapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.dicoding.dicodingeventapp.data.local.entity.EventEntity
import com.dicoding.dicodingeventapp.data.local.room.EventDao
import com.dicoding.dicodingeventapp.data.remote.retrofit.ApiService

class EventRepository private constructor(
    private val apiService: ApiService,
    private val eventDao: EventDao
) {
    fun getUpcomingEvents(): LiveData<Result<List<EventEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getEvents(1)
            val events = response.listEvents
            val eventList = events.map { event ->
                val isFavorited = eventDao.isFavoriteEvent(event.id)
                EventEntity(
                    summary = event.summary,
                    mediaCover = event.mediaCover,
                    registrants = event.registrants,
                    imageLogo = event.imageLogo,
                    link = event.link,
                    description = event.description,
                    ownerName = event.ownerName,
                    cityName = event.cityName,
                    quota = event.quota,
                    name = event.name,
                    id = event.id,
                    beginTime = event.beginTime,
                    endTime = event.endTime,
                    category = event.category,
                    isFavorite = isFavorited,
                    status = 1
                )
            }
            eventDao.deleteEventsByStatus(1)
            Log.d("DATABASE_INSERT", "Inserting ${eventList.size} events")
            eventDao.insertEvent(eventList)
        } catch (e: Exception) {
            Log.d("EventRepository", "getUpcomingEvents: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
        val localData: LiveData<Result<List<EventEntity>>> =
            eventDao.getEventByStatus(1).map { Result.Success(it) }
        emitSource(localData)
    }

    // Fetch limited finished events
    fun getFinishedEvents(): LiveData<Result<List<EventEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getEvents(0)
            val events = response.listEvents
            val eventList = events.map { event ->
                val isFavorited = eventDao.isFavoriteEvent(event.id)
                EventEntity(
                    summary = event.summary,
                    mediaCover = event.mediaCover,
                    registrants = event.registrants,
                    imageLogo = event.imageLogo,
                    link = event.link,
                    description = event.description,
                    ownerName = event.ownerName,
                    cityName = event.cityName,
                    quota = event.quota,
                    name = event.name,
                    id = event.id,
                    beginTime = event.beginTime,
                    endTime = event.endTime,
                    category = event.category,
                    isFavorite = isFavorited,
                    status = 0
                )
            }
            eventDao.deleteEventsByStatus(0)
            Log.d("DATABASE_INSERT", "Inserting ${eventList.size} events")
            eventDao.insertEvent(eventList)
        } catch (e: Exception) {
            Log.d("EventRepository", "getUpcomingEvents: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
        val localData: LiveData<Result<List<EventEntity>>> =
            eventDao.getEventByStatus(0).map { Result.Success(it) }
        emitSource(localData)
    }

    fun getEventByStatus(status: Int): LiveData<Result<List<EventEntity>>> = liveData {
        emit(Result.Loading)
        val localData: LiveData<Result<List<EventEntity>>> =
            eventDao.getEventByStatus(status).map { Result.Success(it) }
        emitSource(localData)
    }

    fun getEventById(id: Int): LiveData<Result<EventEntity>> = liveData {
        emit(Result.Loading)
        val localData: LiveData<Result<EventEntity>> =
            eventDao.getEventById(id).map { Result.Success(it) }
        emitSource(localData)
    }

    fun searchEvents(query: String): LiveData<Result<List<EventEntity>>> = liveData {
        emit(Result.Loading)
        val localData: LiveData<Result<List<EventEntity>>> =
            eventDao.searchEvents(query).map { Result.Success(it) }
        emitSource(localData)
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null
        fun getInstance(
            apiService: ApiService,
            eventDao: EventDao
        ): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(apiService, eventDao)
            }.also { instance = it }
    }
}