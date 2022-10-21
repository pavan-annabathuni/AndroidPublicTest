package com.waycool.weather.utils

class Constants {
    companion object{
        const val BASE_URl = "https://api.openweathermap.org/data/2.5/"
        const val APP_KEY = "b4820b4b64638b57e52d9c5be604b241"
    }
    enum class ApiStatus {LOADING,ERROR,DONE}
}