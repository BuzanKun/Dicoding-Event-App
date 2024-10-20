package com.dicoding.dicodingeventapp.ui

import androidx.lifecycle.ViewModel
import com.dicoding.dicodingeventapp.data.EventRepository

class EventViewModel(private val eventRepository: EventRepository) : ViewModel() {
    fun getUpcomingEvents() = eventRepository.getUpcomingEvents()
    fun getFinishedEvents() = eventRepository.getFinishedEvents()
    fun searchEvents(query: String) = eventRepository.searchEvents(query)
    fun getEventById(id: Int) = eventRepository.getEventById(id)
    fun getEventByStatus(status: Int) = eventRepository.getEventByStatus(status)

    fun calculateRemainingQuota(quota: Int, registrants: Int): Int {
        return quota - registrants
    }
}