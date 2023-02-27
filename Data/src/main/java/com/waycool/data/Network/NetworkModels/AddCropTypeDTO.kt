package com.waycool.data.Network.NetworkModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class AddCropTypeDTO(
    @SerializedName("status"  ) var status  : Boolean?        = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("data"    ) var data: ArrayList<AddCropTypeData> = arrayListOf()
)
@Keep
data class AddCropTypeData(
    @SerializedName("id") var id  : Int?    = null,
    @SerializedName("isSelected") var isSelected:Boolean?=null,
    @SerializedName("soil_type") var soilType : String? = null
)