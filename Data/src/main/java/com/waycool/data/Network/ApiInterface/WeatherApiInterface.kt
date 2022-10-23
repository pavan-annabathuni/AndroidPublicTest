package com.waycool.data.Network.ApiInterface

import com.waycool.data.Network.NetworkModels.WeatherDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiInterface {

    @GET("data/2.5/onecall?exclude=minutely&units=metric")
    suspend fun getWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String,
        @Query("lang") lang: String
    ): Response<WeatherDTO?>

}