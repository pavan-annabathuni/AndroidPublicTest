package com.waycool.data.Network.NetworkModels

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
    val user_feedback: Any? = null
)