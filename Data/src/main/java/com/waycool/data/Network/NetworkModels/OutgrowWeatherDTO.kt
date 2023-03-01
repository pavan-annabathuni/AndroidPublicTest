package com.waycool.data.Network.NetworkModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class OutgrowWeatherDTO(
    @SerializedName("status"  ) var status  : Boolean?        = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("data"    ) var data    : ArrayList<WeatherDataNetwork> = arrayListOf()
)

@Keep
data class DetailsNetwork (

    @SerializedName("dataProvider"   ) var dataProvider   : String? = null,
    @SerializedName("limitPerMinute" ) var limitPerMinute : Int?    = null,
    @SerializedName("iconBaseUrl"    ) var iconBaseUrl    : String? = null

)
@Keep
data class PinLocationNetwork (

    @SerializedName("lat" ) var lat : Double? = null,
    @SerializedName("lon" ) var lon : Double? = null

)
@Keep
data class LocationDetailsNetwork (

    @SerializedName("stateName"       ) var stateName       : String?      = null,
    @SerializedName("districtName"    ) var districtName    : String?      = null,
    @SerializedName("subDistrictName" ) var subDistrictName : String?      = null,
    @SerializedName("villageName"     ) var villageName     : String?      = null,
    @SerializedName("panchayatName"   ) var panchayatName   : String?      = null,
    @SerializedName("panchayatType"   ) var panchayatType   : String?      = null,
    @SerializedName("stateCode"       ) var stateCode       : Int?         = null,
    @SerializedName("districtCode"    ) var districtCode    : Int?         = null,
    @SerializedName("subDistrictCode" ) var subDistrictCode : Int?         = null,
    @SerializedName("villageCode"     ) var villageCode     : Int?         = null,
    @SerializedName("panchayatCode"   ) var panchayatCode   : Int?         = null,
    @SerializedName("pinLocation"     ) var pinLocation     : PinLocationNetwork? = PinLocationNetwork(),
    @SerializedName("id"              ) var id              : String?      = null,
    @SerializedName("distance"        ) var distance        : Double?      = null

)
@Keep
data class CurrentWeatherNetwork (

    @SerializedName("temperature"            ) var temperature            : Double?  = null,
    @SerializedName("iconId"                 ) var iconId                 : String?  = null,
    @SerializedName("pressure"               ) var pressure               : Double?  = null,
    @SerializedName("windSpeed"              ) var windSpeed              : Double?  = null,
    @SerializedName("humidity"               ) var humidity               : Int?     = null,
    @SerializedName("apparentTemperature"    ) var apparentTemperature    : Double?  = null,
    @SerializedName("precipitationIntensity" ) var precipitationIntensity : Int?     = null,
    @SerializedName("precipitationType"      ) var precipitationType      : String?  = null,
    @SerializedName("summary"                ) var summary                : String?  = null,
    @SerializedName("visibility"             ) var visibility             : Double?  = null,
    @SerializedName("date"                   ) var date                   : String?  = null,
    @SerializedName("uvIndex"                ) var uvIndex                : Int?     = null,
    @SerializedName("windDirection"          ) var windDirection          : String?  = null,
    @SerializedName("daylight"               ) var daylight               : Boolean? = null

)
@Keep
data class Hourly (
    @SerializedName("temperature"         ) var temperature         : Double? = null,
    @SerializedName("temperatureApparent" ) var temperatureApparent : Double? = null,
    @SerializedName("iconId"              ) var iconId              : String? = null,
    @SerializedName("humidity"            ) var humidity            : Int?    = null,
    @SerializedName("windGust"            ) var windGust            : Double? = null,
    @SerializedName("windSpeed"           ) var windSpeed           : Double? = null,
    @SerializedName("precipitationAmount" ) var precipitationAmount : Int?    = null,
    @SerializedName("precipitationChance" ) var precipitationChance : Int?    = null,
    @SerializedName("time"                ) var time                : String? = null,
    @SerializedName("pressure"            ) var pressure            : Double? = null,
    @SerializedName("description"         ) var description         : String? = null,
    @SerializedName("cloudCover"          ) var cloudCover          : Int?    = null,
    @SerializedName("visibility"          ) var visibility          : Double? = null,
    @SerializedName("uvIndex"             ) var uvIndex             : Int?    = null,
    @SerializedName("windDirection"       ) var windDirection       : String? = null

)
@Keep
data class DailyWeatherNetwork (

    @SerializedName("apparentTemperatureMin" ) var apparentTemperatureMin : Double? = null,
    @SerializedName("apparentTemperatureMax" ) var apparentTemperatureMax : Double? = null,
    @SerializedName("iconId"                 ) var iconId                 : String? = null,
    @SerializedName("humidity"               ) var humidity               : Int?    = null,
    @SerializedName("windSpeed"              ) var windSpeed              : Double? = null,
    @SerializedName("precipitationIntensity" ) var precipitationIntensity : Int?    = null,
    @SerializedName("precipitationChance"    ) var precipitationChance    : Int?    = null,
    @SerializedName("date"                   ) var date                   : String? = null,
    @SerializedName("description"            ) var description            : String? = null,
    @SerializedName("maxUvIndex"             ) var maxUvIndex             : Int?    = null,
    @SerializedName("sunrise"                ) var sunrise                : String? = null,
    @SerializedName("sunset"                 ) var sunset                 : String? = null

)

@Keep
data class WeatherDataNetwork (

    @SerializedName("details"         ) var details         : DetailsNetwork?          = DetailsNetwork(),
    @SerializedName("locationDetails" ) var locationDetails : LocationDetailsNetwork?  = LocationDetailsNetwork(),
    @SerializedName("current"         ) var current         : CurrentNetwork?          = CurrentNetwork(),
    @SerializedName("hourly"          ) var hourly          : ArrayList<Hourly> = arrayListOf(),
    @SerializedName("daily"           ) var daily           : ArrayList<DailyNetwork>  = arrayListOf()

)
