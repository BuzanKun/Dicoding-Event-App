package com.dicoding.dicodingeventapp.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.dicodingeventapp.data.response.EventResponse
import com.dicoding.dicodingeventapp.data.response.ListEventsItem
import com.dicoding.dicodingeventapp.data.retrofit.ApiConfig
import com.dicoding.dicodingeventapp.ui.util.ToastEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListViewModel : ViewModel() {
    private val _listUpcomingEvent = MutableLiveData<List<ListEventsItem>>()
    val listUpcomingEvent: LiveData<List<ListEventsItem>> = _listUpcomingEvent

    private val _listFinishedEvent = MutableLiveData<List<ListEventsItem>>()
    val listFinishedEvent: LiveData<List<ListEventsItem>> = _listFinishedEvent

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<ToastEvent<String>?>()
    val errorMessage: LiveData<ToastEvent<String>?> = _errorMessage

    companion object {
        private const val TAG = "MainViewModel"
    }

    fun findEvents(active: Int) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getEvents(active)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(
                call: Call<EventResponse>, response: Response<EventResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && active == 1) {
                    _listUpcomingEvent.value = response.body()?.listEvents
                } else if (response.isSuccessful && active == 0) {
                    _listFinishedEvent.value = response.body()?.listEvents
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    _errorMessage.value = ToastEvent("Failed to Catch API Data")
                }
            }

            override fun onFailure(p0: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
                _errorMessage.value = ToastEvent("No Connection, Unable to Fetch API Data")
            }
        })
    }
}