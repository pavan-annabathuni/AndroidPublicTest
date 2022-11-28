package com.waycool.data.repository.domainModels

import com.google.gson.annotations.SerializedName
import com.waycool.data.Network.NetworkModels.CropData

data class AiCropHistoryDomain(
    val crop_id: Int? = null,
    val disease_id: Int? = null,
    val id: Int? = null,
    val image_url: String? = null,
    val prediction: String? = null,
    val probability: Int? = null,
    val updated_at: String? = null,
    val user_feedback: Any? = null,
    @SerializedName("crop") var cropdata: CropData?
)
data class  CropData(
    @SerializedName("crop_name") var name:String?=null
)
