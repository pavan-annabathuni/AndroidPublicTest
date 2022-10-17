package com.example.weather.apiService

import com.example.weather.apiService.apiResponse.Current
import com.example.weather.apiService.apiResponse.WeatherResponse
import com.example.weather.utils.Constants
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(Constants.BASE_URl)
    .build()

interface api {
    @GET("onecall?exclude=minutely&units=metric")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String,
        @Query("lang") lang: String
    ):WeatherResponse

    object weatherApi{
        val retrofitService: api by lazy {
            retrofit.create(api::class.java)
        }
    }
}