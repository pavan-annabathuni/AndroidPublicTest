package com.waycool.data.repository.domainModels.weather

import android.os.Parcelable
import androidx.annotation.Keep
import com.waycool.data.repository.domainModels.WeatherDomain
import kotlinx.parcelize.Parcelize

@Parcelize @Keep
data class HourlyDomain (
    var dt         : Int?               = null,
    var temp       : Double?            = null,
    var feelsLike  : Double?            = null,
    var pressure   : Double?               = null,
    var humidity   : Double?               = null,
    var dewPoint   : Double?            = null,
    var uvi        : Double?               = null,
    var clouds     : Double?               = null,
    var visibility : Double?               = null,
    var windSpeed  : Double?            = null,
    var windDeg    : Double?               = null,
    var windGust   : Double?            = null,
    var weather    : List<WeatherDomain> = emptyList(),
    var pop        : Double?               = null

):Parcelable
