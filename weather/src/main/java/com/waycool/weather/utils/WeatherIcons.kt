package com.waycool.weather.utils

import android.widget.ImageView
import com.waycool.weather.R

object WeatherIcons {
    fun setWeatherIcon(id: String, imageView: ImageView) {
        when (id) {
            "01d" ->
                imageView.setImageResource(R.drawable.ic_wi_day_sunny)
            "01n" -> imageView.setImageResource(R.drawable.ic_wi_night_clear)
            "02d" -> imageView.setImageResource(R.drawable.ic_wi_day_cloudy)
            "02n" -> imageView.setImageResource(R.drawable.ic_wi_day_cloudy)
            "03d" -> imageView.setImageResource(R.drawable.ic_wi_cloud)
            "03n" -> imageView.setImageResource(R.drawable.ic_wi_cloud)
            "04d" -> imageView.setImageResource(R.drawable.ic_wi_cloudy)
            "04n" -> imageView.setImageResource(R.drawable.ic_wi_cloudy)
            "09d" -> imageView.setImageResource(R.drawable.ic_wi_day_rain_mix)
            "09n" -> imageView.setImageResource(R.drawable.ic_wi_thunderstorm)
            "10d"->  imageView.setImageResource(R.drawable.ic_wi_rain)
             "10n" -> imageView.setImageResource(R.drawable.ic_wi_rain)
            "11d" -> imageView.setImageResource(R.drawable.ic_wi_day_lightning)
            "11n" -> imageView.setImageResource(R.drawable.ic_wi_night_lightning)
            "50d" -> imageView.setImageResource(R.drawable.ic_wi_fog)
            "50n" -> imageView.setImageResource(R.drawable.ic_wi_fog)
        }


    }
}