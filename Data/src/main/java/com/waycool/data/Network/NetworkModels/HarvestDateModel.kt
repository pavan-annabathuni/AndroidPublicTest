package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class HarvestDateModel(
    @SerializedName("actual_harvest_date" ) var actualHarvestDate : String? = null,
    @SerializedName("actual_yield"        ) var actualYield       : Int?    = null
)
data class IrrigationPerDay (
    @SerializedName("irrigation"        ) var irrigation     : Int?    = null
        )
