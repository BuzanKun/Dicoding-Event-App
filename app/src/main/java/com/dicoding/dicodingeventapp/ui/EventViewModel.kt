package com.dicoding.dicodingeventapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.dicodingeventapp.data.EventRepository
import com.dicoding.dicodingeventapp.data.local.entity.EventEntity
import kotlinx.coroutines.launch

class EventViewModel(private val eventRepository: EventRepository) : ViewModel() {
    fun getUpcomingEvents() = eventRepository.getUpcomingEvents()
    fun getFinishedEvents() = eventRepository.getFinishedEvents()
    fun searchEvents(query: String) = eventRepository.searchEvents(query)
    fun getEventById(id: Int) = eventRepository.getEventById(id)
    fun getEventByStatus(status: Int) = eventRepository.getEventByStatus(status)

    fun calculateRemainingQuota(quota: Int, registrants: Int): Int {
        return quota - registrants
    }

    fun getFavoriteEvents() = eventRepository.getFavoriteEvents()

    fun saveFavoriteEvent(event: EventEntity) {
        viewModelScope.launch {
            eventRepository.setFavoriteEvent(event, true)
        }
    }

    fun deleteFavoriteEvent(event: EventEntity) {
        viewModelScope.launch {
            eventRepository.setFavoriteEvent(event, false)
        }
    }
}