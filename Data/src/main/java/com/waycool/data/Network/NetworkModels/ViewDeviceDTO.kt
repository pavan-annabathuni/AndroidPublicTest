package com.waycool.data.Network.NetworkModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ViewDeviceDTO(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: ArrayList<ViewDeviceData> = arrayListOf()
)

@Keep
data class ViewDeviceData(
    @SerializedName("id"                        ) var id                    : Int?                   = null,
    @SerializedName("data_timestamp"            ) var dataTimestamp         : String?                = null,
    @SerializedName("temperature"               ) var temperature           : Double?                = null,
    @SerializedName("humidity"                  ) var humidity              : Double?                = null,
    @SerializedName("pressure"                  ) var pressure              : Double?                = null,
    @SerializedName("rainfall"                  ) var rainfall              : Double?                = null,
    @SerializedName("windspeed"                 ) var windspeed             : Double?                = null,
    @SerializedName("wind_direction"            ) var windDirection         : String?                = null,
    @SerializedName("leaf_wetness"              ) var leafWetness           : Int?                   = null,
    @SerializedName("soil_temperature_1"        ) var soilTemperature1      : Double?                = null,
    @SerializedName("soil_temperature_2"        ) var soilTemperature2      : String?                = null,
    @SerializedName("lux"                       ) var lux                   : Double?                = null,
    @SerializedName("model_id"                  ) var modelId               : Int?                   = null,
    @SerializedName("device_elevation"          ) var deviceElevation       :Double?                    = null,
    @SerializedName("serial_no_id"              ) var serialNoId            : Int?                   = null,
    @SerializedName("iot_devices_data_id"       ) var iotDevicesDataId      : Int?                   = null,
    @SerializedName("is_approved"               ) var isApproved            : Int?                   = null,
    @SerializedName("farm_id"                   ) var farmId                : Int?                   = null,
    @SerializedName("soil_moisture_1_kpa"       ) var soilMoisture1Kpa      : Double?                = null,
    @SerializedName("soil_moisture_2_kpa"       ) var soilMoisture2Kpa      : Double?                   = null,
    @SerializedName("device_name"               ) var deviceName            : String?                = null,
    @SerializedName("device_number"             ) var deviceNumber          : String?                = null,
    @SerializedName("delta_t"                   ) var deltaT                : Double?                = null,
    @SerializedName("delta_t_range"         ) var deltaTRange        : RangesData?        = RangesData(),
    @SerializedName("soil_moisture_1_range" ) var soilMoisture1Range : RangesData? = RangesData(),
    @SerializedName("soil_moisture_2_range" ) var soilMoisture2Range : RangesData? = RangesData(),
    @SerializedName("iot_devices_data") var iotDevicesData: ViewIOTData? = ViewIOTData(),
    @SerializedName("model") var model: ViewIOTModelData? = ViewIOTModelData()
)

@Keep
data class ViewIOTData(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("battery") var battery: String? = null
)

@Keep
data class ViewIOTModelData(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("model_name") var modelName: String? = null,
    @SerializedName("series") var series: String? = null
)

@Keep
data class RangesData (

    @SerializedName("min"    ) var min    : Double?    = null,
    @SerializedName("max"    ) var max    : Double?    = null,
    @SerializedName("ranges" ) var ranges : Map<String,List<Double>>? = emptyMap()

)
//data class SoilMoisture1Range (
//
//    @SerializedName("min"    ) var min    : Double?    = null,
//    @SerializedName("max"    ) var max    : Double?    = null,
//    @SerializedName("ranges" ) var ranges : Map<String,List<Double>>? = emptyMap()
//
//)
//data class SoilMoisture2Range (
//
//    @SerializedName("min"    ) var min    : Double?    = null,
//    @SerializedName("max"    ) var max    : Double?    = null,
//    @SerializedName("ranges" ) var ranges : Map<String,List<Double>>? = emptyMap()
//
//)
