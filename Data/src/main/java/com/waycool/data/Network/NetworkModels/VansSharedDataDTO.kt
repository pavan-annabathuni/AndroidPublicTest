package com.waycool.data.Network.NetworkModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class VansSharedDataDTO(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data:VansSharedData = VansSharedData()
)

@Keep
data class  VansSharedData(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("vans_type") var vans_type: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("desc") var desc: String? = null,
    @SerializedName("content_url") var content_url : String? = null,
    @SerializedName("thumbnail_url") var thumbnail_url: String? = null,
    @SerializedName("audio_url") var audio_url: String? = null,
    @SerializedName("source_name") var source_name : String? = null,
    @SerializedName("category_id") var category_id : Int? = null,
    @SerializedName("crop_id") var crop_id : Int? = null,
    @SerializedName("updated_at") var updated_at : String? = null
)