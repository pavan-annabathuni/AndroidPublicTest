package com.waycool.data.Local.Entity

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "crop_master")
data class CropMasterEntity(

    @PrimaryKey var cropId: Int,
    @ColumnInfo(name = "crop_name") var cropName: String? = null,
    @ColumnInfo(name = "crop_category_id") var cropCategory_id: Int? = null,
    @ColumnInfo(name = "crop_logo") var cropLogo: String? = null,
    @ColumnInfo(name = "crop_type") var cropType: String? = null,
    @ColumnInfo(name = "ai_crop_health") var aiCropHealth: Int? = 0,
    @ColumnInfo(name = "water_model") var waterModel: Int? = 0,
    @ColumnInfo(name = "crop_info") var cropInfo: Int? = 0,
    @ColumnInfo(name = "pest_disease_info") var pestDiseaseInfo: Int? = 0,
    @ColumnInfo(name = "disease_prediction") var diseasePrediction: Int? = 0,
    @ColumnInfo(name = "gdd") var gdd: Int? = 0,
    @ColumnInfo(name = "translation") var translation: String? = null,
)