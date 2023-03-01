package com.waycool.data.Network.NetworkModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class LanguageMasterDTO(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: ArrayList<LanguageData> = arrayListOf()
)


@Keep
data class LanguageData(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("lang") var lang: String? = null,
    @SerializedName("lang_native") var langNative: String? = null,
    @SerializedName("lang_code") var langCode: String? = null,
    @SerializedName("lang_icon") var langIcon: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null

)
