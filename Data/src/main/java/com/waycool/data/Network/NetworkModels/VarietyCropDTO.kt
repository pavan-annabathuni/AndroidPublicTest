package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class VarietyCropDTO(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: VarietyCrop? = VarietyCrop()
)
data class VarietyCrop(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("variety_tag") var varietyTag: String? = null,
    @SerializedName("variety") var variety: String? = null,
    @SerializedName("crop_id") var cropId: Int? = null,
    @SerializedName("purpose") var purpose: String? = null,
)
