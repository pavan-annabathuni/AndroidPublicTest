package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class OTPResponseDTO(
    @SerializedName("message")
    @Expose
    var message: String? = null,

    @SerializedName("type")
    @Expose
    var type: String? = null
)