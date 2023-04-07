package com.waycool.data.repository.domainModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CropInformationDomain(
    @SerializedName("data") val `data`: List<CropInformationDomainData>,
    val message: String?,
    val status: Boolean?
)
@Keep
data class CropInformationDomainData(
    val crop_id: Int?,
    val id: Int?,
    val label_name: String?,
    val label_value: String?,
    var labelNameTag  : String? = null,
    var labelValueTag : String? = null,
)
