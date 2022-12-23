package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

class ActivateDeviceDTO (
    @SerializedName("status"  ) var status  : Boolean? = null,
    @SerializedName("message" ) var message : String?  = null,
    @SerializedName("data"  ) var data: ActivateResponseData)

data class ActivateResponseData(
    @SerializedName("account_no_id"    ) var accountNoId     : Int?    = null,
    @SerializedName("serial_no_id"     ) var serialNoId      : Int?    = null,
    @SerializedName("device_name"      ) var deviceName      : String? = null,
    @SerializedName("device_model_id"  ) var deviceModelId   : Int?    = null,
    @SerializedName("is_approved"      ) var isApproved      : Int?    = null,
//    @SerializedName("device_version"   ) var deviceVersion   : String? = null,
    @SerializedName("device_lat"       ) var deviceLat       : String?    = null,
    @SerializedName("device_long"      ) var deviceLong      : String?    = null,
    @SerializedName("device_elevation" ) var deviceElevation : Double?    = null,
    @SerializedName("farm_id"          ) var farmId          : Int?    = null,
    @SerializedName("plot_id"          ) var plotId          : Int?    = null,
    @SerializedName("updated_at"       ) var updatedAt       : String? = null,
    @SerializedName("created_at"       ) var createdAt       : String? = null,
    @SerializedName("id"               ) var id              : Int?    = null
)
//{
//    "status": true,
//    "message": "Success",
//    "data": {
//    "account_no_id": 2,
//    "serial_no_id": 1,
//    "device_name": "Lindsey Harvey",
//    "device_model_id": 3,
//    "is_approved": 1,
//    "device_lat": "12.844847",
//    "device_long": "77.587458",
//    "device_elevation": 930,
//    "farm_id": 1,
//    "plot_id": 4,
//    "updated_at": "2022-12-02 12:50:19",
//    "created_at": "2022-12-02 12:50:19",
//    "id": 24
//}
//}