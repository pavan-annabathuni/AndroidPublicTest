package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class FarmDetailsDTO(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: ArrayList<FarmDetailsData> = arrayListOf()
)
data class FarmDetailsData(
    @SerializedName("Today"    ) var Today    : ArrayList<Today>    = arrayListOf(),
    @SerializedName("Tomorrow" ) var Tomorrow : ArrayList<Tomorrow> = arrayListOf()
)
data class Today(
    @SerializedName("datetime"  ) var datetime  : String? = null,
    @SerializedName("delta_t"   ) var deltaT    : Double? = null,
    @SerializedName("condition" ) var condition : String? = null
)
data class Tomorrow(
    @SerializedName("datetime"  ) var datetime  : String? = null,
    @SerializedName("delta_t"   ) var deltaT    : Double? = null,
    @SerializedName("condition" ) var condition : String? = null
)