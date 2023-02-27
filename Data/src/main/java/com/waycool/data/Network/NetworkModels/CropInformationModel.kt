package com.waycool.data.Network.NetworkModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CropInfo(
    @SerializedName("status"  ) var status  : Boolean?        = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("data"    ) var data    : ArrayList<CropInfoData> = arrayListOf()
)

@Keep
data class CropInfoData(
    @SerializedName("id"              ) var id            : Int?    = null,
    @SerializedName("crop_id"         ) var cropId        : Int?    = null,
    @SerializedName("label_name_tag"  ) var labelNameTag  : String? = null,
    @SerializedName("label_value_tag" ) var labelValueTag : String? = null,
    @SerializedName("label_name"      ) var labelName     : String? = null,
    @SerializedName("label_value"     ) var labelValue    : String? = null

)
