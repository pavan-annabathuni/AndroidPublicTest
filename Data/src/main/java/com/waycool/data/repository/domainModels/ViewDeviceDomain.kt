package com.waycool.data.repository.domainModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ViewDeviceDomain (
        var id                 : Int?                = null,
        var dataTimestamp      : String?             = null,
        var temperature        : Double?             = null,
        var humidity           : Double?             = null,
        var pressure           : Double?             = null,
        var rainfall           : Double?             = null,
        var windspeed          : Double?             = null,
        var soilMoisture1      : Double?                = null,
        var soilMoisture2      : Double?             = null,
        var leafWetness        : Int?             = null,
        var soilTemperature1   : Double?             = null,
        var lux                : Double?             = null,
        var modelId            : Int?                = null,
        var serialNoId         : Int?                = null,
        var iotDevicesDataId   : Int?                = null,
        var farmId             : Int?                = null,
        var deviceName         : String?             = null,
        var deviceElevation:Double?=null,
        var deltaT             : Double?                = null,
        var deltaTRange        : RangesDomain?        = RangesDomain(),
        var soilMoisture1Range : RangesDomain? = RangesDomain(),
        var soilMoisture2Range : RangesDomain? = RangesDomain(),
        var battery            : String? = null,
        var modelName          : String? =null,
        var modelSeries        : String? =null,
         var deviceNumber         : String?             = null,
        var isApproved         : Int?             = null,

        )
@Keep
data class RangesDomain (

      var min    : Double?    = null,
      var max    : Double?    = null,
      var ranges : Map<String,List<Double>>? = emptyMap()

)