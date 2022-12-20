package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class SoilTestHistoryDTO (
    @SerializedName("status"  ) var status  : Boolean?        = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("data"    ) var data: ArrayList<SoilTestData> = arrayListOf()
)

data class SoilTestData(
@SerializedName("id"               ) var id             : Int?    = null,
@SerializedName("plot_no"          ) var plotNo         : String? = null,
@SerializedName("soil_test_number" ) var soilTestNumber : String? = null,
@SerializedName("status"           ) var status         : String? = null,
@SerializedName("approve_status"   ) var approveStatus  : String? = null,
@SerializedName("date"       ) var updatedAt      : String? = null
)