package com.waycool.data.Repository.DomainModels

import com.google.gson.annotations.SerializedName

data class CropInformationDomain(
    @SerializedName("data") val `data`: List<CropInformationDomainData>,
    val message: String?,
    val status: Boolean?
)

data class CropInformationDomainData(
    val crop_id: Int?,
    val id: Int?,
    val label_name: String?,
    val label_value: String?
)
