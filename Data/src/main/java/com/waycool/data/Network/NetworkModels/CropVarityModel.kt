package com.waycool.data.Network.NetworkModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class CropVarietyModel(
    @SerializedName("state_name")
    var stateName: String?= null,
    @SerializedName("crop_variety_value")
    var cropVarietyValue : ArrayList<String> = arrayListOf()
)