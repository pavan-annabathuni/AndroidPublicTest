package com.waycool.data.Network.NetworkModels

data class CropInfo(val `data`: List<CropInfoData>,
                    val message: String?,
                    val status: Boolean?)

data class CropInfoData(
    val crop_id: Int?,
    val id: Int?,
    val label_name: String?,
    val label_value: String?
)
