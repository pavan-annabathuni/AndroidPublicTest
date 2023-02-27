package com.waycool.data.Network.NetworkModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class HarvestDateModel(
    @SerializedName("actual_harvest_date" ) var actualHarvestDate : String? = null,
    @SerializedName("actual_yield"        ) var actualYield       : Int?    = null
)
@Keep
data class IrrigationPerDay (
    @SerializedName("irrigation"        ) var irrigation     : Int?    = null
        )
