package com.waycool.data.Local.Entity

import androidx.annotation.Keep

@Keep
data class WeatherMasterEntity(
    var lat: Double? = null,
    var lon: Double? = null,
    var timezone: String? = null,
    var timezoneOffset: Int? = null,
    var current: CurrentEntity? = CurrentEntity(),
    var hourly: List<HourlyEntity> = emptyList(),
    var daily: List<DailyEntity> = emptyList()

)
@Keep
data class WeatherEntity(

    var id: Int? = null,
    var main: String? = null,
    var description: String? = null,
    var icon: String? = null

)
@Keep
data class CurrentEntity(

    var dt: Int? = null,
    var sunrise: Int? = null,
    var sunset: Int? = null,
    var temp: Double? = null,
    var feelsLike: Double? = null,
    var pressure: Double? = null,
    var humidity: Double? = null,
    var dewPoint: Double? = null,
    var uvi: Double? = null,
    var clouds: Double? = null,
    var visibility: Double? = null,
    var windSpeed: Double? = null,
    var windDeg: Double? = null,
    var weather: List<WeatherEntity> = arrayListOf()

)
@Keep
data class HourlyEntity(
    var dt: Int? = null,
    var temp: Double? = null,
    var feelsLike: Double? = null,
    var pressure: Double? = null,
    var humidity: Double? = null,
    var dewPoint: Double? = null,
    var uvi: Double? = null,
    var clouds: Double? = null,
    var visibility: Double? = null,
    var windSpeed: Double? = null,
    var windDeg: Double? = null,
    var windGust: Double? = null,
    var weather: List<WeatherEntity> = arrayListOf(),
    var pop: Double? = null

)

@Keep
data class TempEntity(

    var day: Double? = null,
    var min: Double? = null,
    var max: Double? = null,
    var night: Double? = null,
    var eve: Double? = null,
    var morn: Double? = null

)
@Keep
data class FeelsLikeEntity(

    var day: Double? = null,
    var night: Double? = null,
    var eve: Double? = null,
    var morn: Double? = null

)

@Keep
data class DailyEntity(

    var dt: Int? = null,
    var sunrise: Int? = null,
    var sunset: Int? = null,
    var moonrise: Int? = null,
    var moonset: Int? = null,
    var moonPhase: Double? = null,
    var temp: TempEntity? = TempEntity(),
    var feelsLike: FeelsLikeEntity? = FeelsLikeEntity(),
    var pressure: Double? = null,
    var humidity: Double? = null,
    var dewPoint: Double? = null,
    var windSpeed: Double? = null,
    var windDeg: Double? = null,
    var windGust: Double? = null,
    var weather: List<WeatherEntity> = arrayListOf(),
    var clouds: Double? = null,
    var pop: Double? = null,
    var rain: Double? = null,
    var uvi: Double? = null
)