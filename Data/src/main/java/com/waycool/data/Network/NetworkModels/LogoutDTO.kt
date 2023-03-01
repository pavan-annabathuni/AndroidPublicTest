package com.waycool.data.Network.NetworkModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class LogoutDTO(
    @SerializedName("status") var status: Boolean?=null,
    @SerializedName("message") var message: String?=null,
    @SerializedName("data") var data: String?=null
)
