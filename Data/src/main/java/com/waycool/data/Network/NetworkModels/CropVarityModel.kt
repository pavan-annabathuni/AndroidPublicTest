package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

class CropVarietyModel(
    @SerializedName("state_name")
    var stateName: String?= null,
    @SerializedName("crop_variety_value")
    var cropVarietyValue : ArrayList<String> = arrayListOf()
)