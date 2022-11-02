package com.waycool.data.repository.domainModels

import com.google.gson.annotations.SerializedName

class CropVarityDomain {
    @SerializedName("state_name"         ) var stateName        : String?           = null
    @SerializedName("crop_variety_value" ) var cropVarietyValue : List<String> = listOf()
}