package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class AiCropHistoryDTO(
    val data: List<AiHistoryData>?,
    val message: String?,
    val status: Boolean?
)

data class AiHistoryData(
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