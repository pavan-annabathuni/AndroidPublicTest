package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName


data class CropMasterDTO(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: ArrayList<CropMasterData> = arrayListOf()
)
data class CropMasterData(
    @SerializedName("crop_id") var cropId: Int? = null,
    @SerializedName("crop_name") var cropName: String? = null,
    @SerializedName("crop_category_id") var cropCategory_id: Int? = null,
    @SerializedName("crop_logo") var cropLogo: String? = null,
    @SerializedName("crop_type") var cropType: String? = null,
    @SerializedName("is_ai_crop_health") var aiCropHealth: Int? = null,
    @SerializedName("is_water_model") var isWaterModel: Int? = null,
    @SerializedName("is_crop_info") var isCropInfo: Int? = null,
    @SerializedName("is_pest_disease_info") var isPestDiseaseInfo: Int? = null,
    @SerializedName("is_disease_prediction") var isDiseasePrediction: Int? = null,
    @SerializedName("is_gdd") var isGdd: Int? = null,
    @SerializedName("translation") var translation: String? = null,
    @SerializedName("updated_at") var updated_at: String? = null,
    @SerializedName("crop_name_tag") var cropNameTag: String? = null,

)
