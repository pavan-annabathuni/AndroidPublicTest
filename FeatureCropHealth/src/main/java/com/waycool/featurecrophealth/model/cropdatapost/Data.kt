package com.waycool.featurecrophealth.model.cropdatapost

data class Data(
    val crop_id: String,
    val disease_id: Int,
    val id: Int,
    val image_url: String,
    val prediction: String,
    val probability: String,
    val updated_at: String
)