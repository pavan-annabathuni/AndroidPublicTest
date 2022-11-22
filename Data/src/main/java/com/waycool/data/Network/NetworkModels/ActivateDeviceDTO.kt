package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

class ActivateDeviceDTO (
    @SerializedName("status"  ) var status  : Boolean? = null,
    @SerializedName("message" ) var message : String?  = null,
    @SerializedName("data"    ) var data: ActivateResponseData)

data class ActivateResponseData(
    @SerializedName("account_no_id"    ) var accountNoId     : Int?    = null,
    @SerializedName("serial_no_id"     ) var serialNoId      : Int?    = null,
    @SerializedName("device_name"      ) var deviceName      : String? = null,
    @SerializedName("device_model_id"  ) var deviceModelId   : Int?    = null,
    @SerializedName("is_approved"      ) var isApproved      : Int?    = null,
    @SerializedName("device_version"   ) var deviceVersion   : String? = null,
    @SerializedName("device_lat"       ) var deviceLat       : Int?    = null,
    @SerializedName("device_long"      ) var deviceLong      : Int?    = null,
    @SerializedName("device_elevation" ) var deviceElevation : Int?    = null,
    @SerializedName("farm_id"          ) var farmId          : Int?    = null,
    @SerializedName("plot_id"          ) var plotId          : Int?    = null,
    @SerializedName("updated_at"       ) var updatedAt       : String? = null,
    @SerializedName("created_at"       ) var createdAt       : String? = null,
    @SerializedName("id"               ) var id              : Int?    = null
)