package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class ViewDeviceDTO (
        @SerializedName("status"  ) var status  : Boolean?        = null,
        @SerializedName("message" ) var message : String?         = null,
        @SerializedName("data"    ) var data    : ArrayList<ViewDeviceData> = arrayListOf()
)
data class ViewDeviceData(
        @SerializedName("id"                  ) var id               : Int?            = null,
        @SerializedName("data_timestamp"      ) var dataTimestamp    : String?         = null,
        @SerializedName("temperature"         ) var temperature      : String?         = null,
        @SerializedName("humidity"            ) var humidity         : String?         = null,
        @SerializedName("pressure"            ) var pressure         : String?         = null,
        @SerializedName("rainfall"            ) var rainfall         : String?         = null,
        @SerializedName("windspeed"           ) var windspeed        : String?         = null,
        @SerializedName("wind_direction"      ) var windDirection    : String?         = null,
        @SerializedName("soil_moisture_1"     ) var soilMoisture1    : String?         = null,
        @SerializedName("soil_moisture_2"     ) var soilMoisture2    : String?         = null,
        @SerializedName("leaf_wetness"        ) var leafWetness      : String?         = null,
        @SerializedName("soil_temperature_1"  ) var soilTemperature1 : String?         = null,
        @SerializedName("soil_temperature_2"  ) var soilTemperature2 : String?         = null,
        @SerializedName("lux"                 ) var lux              : String?         = null,
        @SerializedName("tds"                 ) var tds              : String?         = null,
        @SerializedName("ph"                  ) var ph               : String?         = null,
        @SerializedName("sensor_value_1"      ) var sensorValue1     : String?         = null,
        @SerializedName("sensor_value_2"      ) var sensorValue2     : String?         = null,
        @SerializedName("created_at"          ) var createdAt        : String?         = null,
        @SerializedName("updated_at"          ) var updatedAt        : String?         = null,
        @SerializedName("model_id"            ) var modelId          : Int?            = null,
        @SerializedName("serial_no_id"        ) var serialNoId       : Int?            = null,
        @SerializedName("iot_devices_data_id" ) var iotDevicesDataId : Int?            = null,
        @SerializedName("device_name"         ) var deviceName       : String?         = null,
        @SerializedName("iot_devices_data"    ) var iotDevicesData   : ViewIOTData? = ViewIOTData(),
        @SerializedName("model"               ) var model            : ViewIOTModel?          = ViewIOTModel()
)
data class ViewIOTData(
        @SerializedName("id") var id      : Int?    = null,
        @SerializedName("battery" ) var battery : String? = null
)
data class ViewIOTModel(
        @SerializedName("id"         ) var id        : Int?    = null,
        @SerializedName("model_name" ) var modelName : String? = null
)


