package com.dicoding.dicodingeventapp.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.dicodingeventapp.data.response.DetailResponse
import com.dicoding.dicodingeventapp.data.response.EventDetail
import com.dicoding.dicodingeventapp.data.retrofit.ApiConfig
import com.dicoding.dicodingeventapp.ui.util.Event

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel : ViewModel() {

    private val _eventDetail = MutableLiveData<EventDetail?>()
    val eventDetail: MutableLiveData<EventDetail?> = _eventDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<Event<String>?>()
    val errorMessage: LiveData<Event<String>?> = _errorMessage

    companion object {
        private const val TAG = "DetailViewModel"
    }

    fun findEventDetail(id: Int) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getDetailEvent(id)
        client.enqueue(object : Callback<DetailResponse> {
            override fun onResponse(
                call: Call<DetailResponse>,
                response: Response<DetailResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _eventDetail.value = response.body()?.eventDetail
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    _errorMessage.value = Event("Failed to Catch API Data")
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
                _errorMessage.value = Event("No Connection, Unable to Fetch API Data")
            }
        })
    }

    fun calculateRemainingQuota(fullQuota: Int, boughtQuota: Int): Int {
        return fullQuota - boughtQuota
    }
}