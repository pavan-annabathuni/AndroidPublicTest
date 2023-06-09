package com.waycool.data.Network.NetworkModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TrackerDTO(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: ArrayList<TrackerData> = arrayListOf()
)

@Keep
data class TrackerData(
    val is_approved: Int? = null,
    val date: Any? = null,
    val id: Int? = null,
    val title: String? = null
)