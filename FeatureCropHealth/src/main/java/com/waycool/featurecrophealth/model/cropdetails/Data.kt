package com.waycool.featurecrophealth.model.cropdetails

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "details"
)
data class Data(
    @PrimaryKey(autoGenerate = true)
    val crop_id: Int,
    val crop_category_id: Int,
    val crop_logo: String ,
    val crop_name: String ,
    val crop_type: String ,
    val is_ai_crop_health: Int,
    val is_crop_info: Int ,
    val is_disease_prediction: Int,
    val is_gdd: Int?,
    val is_pest_disease_info: Int,
    val is_water_model: Int ,

    val updated_at: String
)