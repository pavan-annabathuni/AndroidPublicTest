package com.waycool.data.Network.NetworkModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

@Keep
data class TagsAndKeywordsDTO(
    @SerializedName("status")
    @Expose
    var isStatus: Boolean = false,

    @SerializedName("message")
    @Expose
    var message: String? = null,

    @SerializedName("data")
    @Expose
    var data: List<TagsData>? = null
)

@Keep
data class TagsData(
    @SerializedName("id")
    @Expose
    var id: Int = 0,

    @SerializedName("tag_name")
    @Expose
    var tag_name: String? = null,

    @SerializedName("translation")
    @Expose
    var translation: String? = null
)