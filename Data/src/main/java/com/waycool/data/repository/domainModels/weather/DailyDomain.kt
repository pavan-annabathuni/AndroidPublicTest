package com.waycool.data.repository.domainModels.weather

import android.os.Parcelable
import androidx.annotation.Keep
import com.waycool.data.repository.domainModels.FeelsLikeDomain
import com.waycool.data.repository.domainModels.TempDomain
import com.waycool.data.repository.domainModels.WeatherDomain
import kotlinx.parcelize.Parcelize

@Parcelize @Keep
data class DailyDomain (

    var dt        : Int?               = null,
    var sunrise   : Int?               = null,
    var sunset    : Int?               = null,
    var moonrise  : Int?               = null,
    var moonset   : Int?               = null,
    var moonPhase : Double?            = null,
    var temp      : TempDomain?              = TempDomain(),
    var feelsLike : FeelsLikeDomain?         = FeelsLikeDomain(),
    var pressure  : Double?               = null,
    var humidity  : Double?               = null,
    var dewPoint  : Double?            = null,
    var windSpeed : Double?            = null,
    var windDeg   : Double?               = null,
    var windGust  : Double?            = null,
    var weather   : List<WeatherDomain> = emptyList(),
    var clouds    : Double?               = null,
    var pop       : Double?            = null,
    var rain      : Double?            = null,
    var uvi       : Double?               = null

):Parcelable
