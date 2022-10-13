package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class AiCropDetectionDTO(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: AiCropDetectionData? = AiCropDetectionData()
)

data class AiCropDetectionData(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("image_url") var imageUrl: String? = null,
    @SerializedName("prediction") var prediction: String? = null,
    @SerializedName("probability") var probability: String? = null,
    @SerializedName("crop_id") var cropId: String? = null,
    @SerializedName("disease_id") var diseaseId: Int? = null,
    @SerializedName("updated_at") var updatedAt: String? = null

)