package com.waycool.data.Local.Entity

data class AiCropHistoryEntity(
    val id: Int? = null,
    val crop_id: Int? = null,
    val disease_id: Int? = null,
    val image_url: String? = null,
    val prediction: String? = null,
    val probability: Int? = null,
    val user_feedback: Any? = null,
    val updated_at: String? = null
    )
