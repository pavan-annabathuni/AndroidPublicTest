package com.waycool.featurecrophealth.model.historydata

data class Data(
    val crop_id: Int,
    val disease_id: Int,
    val id: Int,
    val image_url: String,
    val prediction: String,
    val probability: Int,
    val updated_at: String,
    val user_feedback: Any
)