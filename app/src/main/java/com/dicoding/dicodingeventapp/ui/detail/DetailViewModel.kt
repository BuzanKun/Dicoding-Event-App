package com.dicoding.dicodingeventapp.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.dicodingeventapp.data.response.DetailResponse
import com.dicoding.dicodingeventapp.data.response.Event
import com.dicoding.dicodingeventapp.data.retrofit.ApiConfig
import com.dicoding.dicodingeventapp.ui.util.ToastEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel: ViewModel() {

    private val _eventDetail = MutableLiveData<Event?>()
    val eventDetail: MutableLiveData<Event?> = _eventDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<ToastEvent<String>?>()
    val errorMessage: LiveData<ToastEvent<String>?> = _errorMessage

    companion object{
        private const val TAG = "DetailViewModel"
    }

    fun findEventDetail(id: Int) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getDetailEvent(id)
        client.enqueue(object : Callback<DetailResponse> {
            override fun onResponse(call: Call<DetailResponse>, response: Response<DetailResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _eventDetail.value = response.body()?.event
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    _errorMessage.value = ToastEvent("Failed to Catch API Data")
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
                _errorMessage.value = ToastEvent("No Connection, Unable to Fetch API Data")
            }
        })
    }

}