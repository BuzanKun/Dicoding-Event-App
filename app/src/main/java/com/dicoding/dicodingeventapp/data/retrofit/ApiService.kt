package com.dicoding.dicodingeventapp.data.retrofit

import com.dicoding.dicodingeventapp.data.response.DetailResponse
import com.dicoding.dicodingeventapp.data.response.EventResponse
import retrofit2.http.*
import retrofit2.Call

interface ApiService {
    @GET("events")
    fun getEvents(
        @Query("active") active: Int,
    ): Call<EventResponse>

    @GET("events/{id}")
    fun getDetailEvent(
        @Path("id") id: Int
    ): Call<DetailResponse>

    @GET("events")
    fun searchEvents(
        @Query("active") active: Int = -1,
        @Query("q") keyword: String?
    ): Call<EventResponse>

}