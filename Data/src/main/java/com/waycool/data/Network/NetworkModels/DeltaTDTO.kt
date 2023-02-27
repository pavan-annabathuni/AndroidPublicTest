package com.waycool.data.Network.NetworkModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName


@Keep
data class DeltaTDTO(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: DeltaTData = DeltaTData()
)
@Keep
data class DeltaTData(
    @SerializedName("Today"    ) var Today    : ArrayList<DeltaT>    = arrayListOf(),
    @SerializedName("Tomorrow" ) var Tomorrow : ArrayList<DeltaT> = arrayListOf()
)
@Keep
data class DeltaT(
    @SerializedName("datetime"  ) var datetime  : String? = null,
    @SerializedName("delta_t"   ) var deltaT    : Double? = null,
    @SerializedName("condition" ) var condition : String? = null
)