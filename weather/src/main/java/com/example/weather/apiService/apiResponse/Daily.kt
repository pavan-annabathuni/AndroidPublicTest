package com.example.weather.apiService.apiResponse

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Daily(
    val clouds: Int,
    val dew_point: Double,
    val dt: Int,
    val feels_like: @RawValue FeelsLike,
    val humidity: Int,
    val moon_phase: Double,
    val moonrise: Int,
    val moonset: Int,
    val pop: Double,
    val pressure: Int,
    val rain: Double,
    val sunrise: Int,
    val sunset: Int,
    val temp: @RawValue Temp? = null,
    val uvi: Double,
    val weather: @RawValue List<Weather>,
    val wind_deg: Int,
    val wind_gust: Double,
    val wind_speed: Double
):Parcelable