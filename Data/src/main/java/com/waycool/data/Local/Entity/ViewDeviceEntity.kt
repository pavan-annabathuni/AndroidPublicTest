package com.waycool.data.Local.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.waycool.data.Network.NetworkModels.*

@Entity(tableName = "my_devices")
data class ViewDeviceEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id"                    ) var id                 : Int?                = null,
    @ColumnInfo(name = "data_timestamp"        ) var dataTimestamp      : String?             = null,
    @ColumnInfo(name = "temperature"           ) var temperature        : String?             = null,
    @ColumnInfo(name = "humidity"              ) var humidity           : String?             = null,
    @ColumnInfo(name = "pressure"              ) var pressure           : String?             = null,
    @ColumnInfo(name = "rainfall"              ) var rainfall           : String?             = null,
    @ColumnInfo(name = "windspeed"             ) var windspeed          : String?             = null,
    @ColumnInfo(name = "soil_moisture_1"       ) var soilMoisture1      : Double?                = null,
    @ColumnInfo(name = "soil_moisture_2"       ) var soilMoisture2      : Double?             = null,
    @ColumnInfo(name = "leaf_wetness"          ) var leafWetness        : String?             = null,
    @ColumnInfo(name = "soil_temperature_1"    ) var soilTemperature1   : String?             = null,
    @ColumnInfo(name = "lux"                   ) var lux                : String?             = null,
    @ColumnInfo(name = "model_id"              ) var modelId            : Int?                = null,
    @ColumnInfo(name = "serial_no_id"          ) var serialNoId         : Int?                = null,
    @ColumnInfo(name = "iot_devices_data_id"   ) var iotDevicesDataId   : Int?                = null,
    @ColumnInfo(name = "farm_id"               ) var farmId             : Int?                = null,
    @ColumnInfo(name = "device_name"           ) var deviceName         : String?             = null,
    @ColumnInfo(name = "delta_t"               ) var deltaT             : Double?                = null,
    @ColumnInfo(name = "delta_t_range"         ) var deltaTRange        : RangesEntity?        = RangesEntity(),
    @ColumnInfo(name = "soil_moisture_1_range" ) var soilMoisture1Range : RangesEntity? = RangesEntity(),
    @ColumnInfo(name = "soil_moisture_2_range" ) var soilMoisture2Range : RangesEntity? = RangesEntity(),
    @ColumnInfo(name = "battery"               ) var battery            : String? = null,
    @ColumnInfo(name = "model_name"            ) var modelName          : String? =null,
    @ColumnInfo(name = "model_series"          ) var modelSeries        : String? =null,

    @ColumnInfo(name = "device_number"           ) var deviceNumber         : String?             = null,
    @ColumnInfo(name = "is_approved"           ) var isApproved         : Int?             = null,

)

data class RangesEntity (

    @SerializedName("min"    ) var min    : Double?    = null,
    @SerializedName("max"    ) var max    : Double?    = null,
    @SerializedName("ranges" ) var ranges : Map<String,List<Double>>? = emptyMap()

)
