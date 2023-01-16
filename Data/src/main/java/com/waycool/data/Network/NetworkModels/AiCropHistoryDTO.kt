package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class AiCropHistoryDTO(
    @SerializedName("status"  ) var status  : Boolean?        = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("data"    ) var data    : ArrayList<AiHistoryData> = arrayListOf()
)

data class AiHistoryData(
//    val crop_id: Int? = null,
//    val disease_id: Int? = null,
//    val id: Int? = null,
//    val image_url: String? = null,
//    val prediction: String? = null,
//    val probability: Int? = null,
//    val updated_at: String? = null,
//    val user_feedback: Any? = null,
//    @SerializedName("crop") var cropdata: CropData?
    @SerializedName("id"            ) var id: Int?    = null,
    @SerializedName("image_url"     ) var imageUrl: String? = null,
    @SerializedName("prediction"    ) var prediction: String? = null,
    @SerializedName("probability"   ) var probability: Double? = null,
    @SerializedName("user_feedback" ) var userFeedback: String? = null,
    @SerializedName("disease_id"    ) var diseaseId: Int?    = null,
    @SerializedName("crop_id"       ) var cropId: Int?    = null,
    @SerializedName("updated_at"    ) var updatedAt: String? = null,
    @SerializedName("crop"          ) var crop: CropDataData = CropDataData()
    )

  data class  CropDataData(
      @SerializedName("id"        ) var id       : Int?    = null,
      @SerializedName("crop_name" ) var cropName : String? = null
  )