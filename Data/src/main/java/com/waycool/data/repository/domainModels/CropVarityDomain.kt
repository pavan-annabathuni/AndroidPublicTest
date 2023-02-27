package com.waycool.data.repository.domainModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
class CropVarityDomain {
    @SerializedName("state_name"         ) var stateName        : String?           = null
    @SerializedName("crop_variety_value" ) var cropVarietyValue : List<String> = listOf()
}