package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class WeatherDTO (

    @SerializedName("lat"             ) var lat            : Double?           = null,
    @SerializedName("lon"             ) var lon            : Double?           = null,
    @SerializedName("timezone"        ) var timezone       : String?           = null,
    @SerializedName("timezone_offset" ) var timezoneOffset : Int?              = null,
    @SerializedName("current"         ) var current        : CurrentNetwork?          = CurrentNetwork(),
    @SerializedName("hourly"          ) var hourly         : List<HourlyNetwork> = arrayListOf(),
    @SerializedName("daily"           ) var daily          : List<DailyNetwork>  = arrayListOf()

)

data class WeatherNetwork (

    @SerializedName("id"          ) var id          : Int?    = null,
    @SerializedName("main"        ) var main        : String? = null,
    @SerializedName("description" ) var description : String? = null,
    @SerializedName("icon"        ) var icon        : String? = null

)


data class CurrentNetwork (

    @SerializedName("dt"         ) var dt         : Int?               = null,
    @SerializedName("sunrise"    ) var sunrise    : Int?               = null,
    @SerializedName("sunset"     ) var sunset     : Int?               = null,
    @SerializedName("temp"       ) var temp       : Double?            = null,
    @SerializedName("feels_like" ) var feelsLike  : Double?            = null,
    @SerializedName("pressure"   ) var pressure   : Double?               = null,
    @SerializedName("humidity"   ) var humidity   : Double?               = null,
    @SerializedName("dew_point"  ) var dewPoint   : Double?            = null,
    @SerializedName("uvi"        ) var uvi        : Double?               = null,
    @SerializedName("clouds"     ) var clouds     : Double?               = null,
    @SerializedName("visibility" ) var visibility : Double?               = null,
    @SerializedName("wind_speed" ) var windSpeed  : Double?               = null,
    @SerializedName("wind_deg"   ) var windDeg    : Double?               = null,
    @SerializedName("weather"    ) var weather    : List<WeatherNetwork> = arrayListOf()

)

data class HourlyNetwork (

    @SerializedName("dt"         ) var dt         : Int?               = null,
    @SerializedName("temp"       ) var temp       : Double?            = null,
    @SerializedName("feels_like" ) var feelsLike  : Double?            = null,
    @SerializedName("pressure"   ) var pressure   : Double?               = null,
    @SerializedName("humidity"   ) var humidity   : Double?               = null,
    @SerializedName("dew_point"  ) var dewPoint   : Double?            = null,
    @SerializedName("uvi"        ) var uvi        : Double?               = null,
    @SerializedName("clouds"     ) var clouds     : Double?               = null,
    @SerializedName("visibility" ) var visibility : Double?               = null,
    @SerializedName("wind_speed" ) var windSpeed  : Double?            = null,
    @SerializedName("wind_deg"   ) var windDeg    : Double?               = null,
    @SerializedName("wind_gust"  ) var windGust   : Double?            = null,
    @SerializedName("weather"    ) var weather    : List<WeatherNetwork> = arrayListOf(),
    @SerializedName("pop"        ) var pop        : Double?               = null

)


data class TempNetwork (

    @SerializedName("day"   ) var day   : Double? = null,
    @SerializedName("min"   ) var min   : Double? = null,
    @SerializedName("max"   ) var max   : Double? = null,
    @SerializedName("night" ) var night : Double? = null,
    @SerializedName("eve"   ) var eve   : Double? = null,
    @SerializedName("morn"  ) var morn  : Double? = null

)

data class FeelsLikeNetwork (

    @SerializedName("day"   ) var day   : Double? = null,
    @SerializedName("night" ) var night : Double? = null,
    @SerializedName("eve"   ) var eve   : Double? = null,
    @SerializedName("morn"  ) var morn  : Double? = null

)



data class DailyNetwork (

    @SerializedName("dt"         ) var dt        : Int?               = null,
    @SerializedName("sunrise"    ) var sunrise   : Int?               = null,
    @SerializedName("sunset"     ) var sunset    : Int?               = null,
    @SerializedName("moonrise"   ) var moonrise  : Int?               = null,
    @SerializedName("moonset"    ) var moonset   : Int?               = null,
    @SerializedName("moon_phase" ) var moonPhase : Double?            = null,
    @SerializedName("temp"       ) var temp      : TempNetwork?              = TempNetwork(),
    @SerializedName("feels_like" ) var feelsLike : FeelsLikeNetwork?         = FeelsLikeNetwork(),
    @SerializedName("pressure"   ) var pressure  : Double?               = null,
    @SerializedName("humidity"   ) var humidity  : Double?               = null,
    @SerializedName("dew_point"  ) var dewPoint  : Double?            = null,
    @SerializedName("wind_speed" ) var windSpeed : Double?            = null,
    @SerializedName("wind_deg"   ) var windDeg   : Double?               = null,
    @SerializedName("wind_gust"  ) var windGust  : Double?            = null,
    @SerializedName("weather"    ) var weather   : ArrayList<WeatherNetwork> = arrayListOf(),
    @SerializedName("clouds"     ) var clouds    : Double?               = null,
    @SerializedName("pop"        ) var pop       : Double?            = null,
    @SerializedName("rain"       ) var rain      : Double?            = null,
    @SerializedName("uvi"        ) var uvi       : Double?               = null

)