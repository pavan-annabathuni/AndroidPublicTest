package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class MyCrop(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null
)
