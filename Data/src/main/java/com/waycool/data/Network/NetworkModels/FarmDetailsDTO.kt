package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class FarmDetailsDTO(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: FarmDetailsData = FarmDetailsData()
)
data class FarmDetailsData(
    @SerializedName("Today"    ) var Today    : ArrayList<DeltaT>    = arrayListOf(),
    @SerializedName("Tomorrow" ) var Tomorrow : ArrayList<DeltaT> = arrayListOf()
)
data class DeltaT(
    @SerializedName("datetime"  ) var datetime  : String? = null,
    @SerializedName("delta_t"   ) var deltaT    : Double? = null,
    @SerializedName("condition" ) var condition : String? = null
)