package com.waycool.data.repository.domainModels

import android.os.Parcelable
import com.waycool.data.repository.domainModels.weather.DailyDomain
import com.waycool.data.repository.domainModels.weather.HourlyDomain
import kotlinx.parcelize.Parcelize


data class WeatherMasterDomain(

    var lat            : Double?           = null,
    var lon            : Double?           = null,
    var timezone       : String?           = null,
    var timezoneOffset : Int?              = null,
    var current        : CurrentDomain?    = CurrentDomain(),
    var hourly         : List<HourlyDomain> = emptyList(),
    var daily          : List<DailyDomain>  = emptyList()

)

@Parcelize
 data class WeatherDomain (

    var id          : Int?    = null,
    var main        : String? = null,
    var description : String? = null,
    var icon        : String? = null

):Parcelable

data class CurrentDomain (

    var dt         : Int?               = null,
    var sunrise    : Int?               = null,
    var sunset     : Int?               = null,
    var temp       : Double?            = null,
    var feelsLike  : Double?            = null,
    var pressure   : Double?               = null,
    var humidity   : Double?               = null,
    var dewPoint   : Double?            = null,
    var uvi        : Double?               = null,
    var clouds     : Double?               = null,
    var visibility : Double?               = null,
    var windSpeed  : Double?               = null,
    var windDeg    : Double?               = null,
    var weather    : List<WeatherDomain> = emptyList()

)



@Parcelize
data class TempDomain (

    var day   : Double? = null,
    var min   : Double? = null,
    var max   : Double? = null,
    var night : Double? = null,
    var eve   : Double? = null,
    var morn  : Double? = null

):Parcelable

@Parcelize
data class FeelsLikeDomain (

    var day   : Double? = null,
    var night : Double? = null,
    var eve   : Double? = null,
    var morn  : Double? = null

):Parcelable



