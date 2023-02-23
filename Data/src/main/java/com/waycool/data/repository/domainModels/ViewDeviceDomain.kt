package com.waycool.data.repository.domainModels

import com.google.gson.annotations.SerializedName


data class ViewDeviceDomain (
        var id                 : Int?                = null,
        var dataTimestamp      : String?             = null,
        var temperature        : String?             = null,
        var humidity           : String?             = null,
        var pressure           : String?             = null,
        var rainfall           : String?             = null,
        var windspeed          : String?             = null,
        var soilMoisture1      : Double?                = null,
        var soilMoisture2      : Double?             = null,
        var leafWetness        : Int?             = null,
        var soilTemperature1   : String?             = null,
        var lux                : String?             = null,
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

data class RangesDomain (

      var min    : Double?    = null,
      var max    : Double?    = null,
      var ranges : Map<String,List<Double>>? = emptyMap()

)