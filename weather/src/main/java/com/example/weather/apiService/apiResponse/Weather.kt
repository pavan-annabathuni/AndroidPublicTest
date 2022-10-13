package com.example.weather.apiService.apiResponse

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)