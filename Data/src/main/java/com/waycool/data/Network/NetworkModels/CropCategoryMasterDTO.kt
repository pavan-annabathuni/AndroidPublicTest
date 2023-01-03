package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class CropCategoryMasterDTO(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: List<CropCategoryData> = arrayListOf()
)

data class CropCategoryData(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("category_name") var categoryName: String? = null,
    @SerializedName("translation") var translation: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("category_name_tag") var categoryNameTag: String? = null

)
