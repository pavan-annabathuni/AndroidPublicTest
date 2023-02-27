package com.waycool.data.Local.Entity

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class LanguageMasterEntity(
    var id: Int? = null,
    var lang: String? = null,
    var langNative: String? = null,
    var langCode: String? = null,
    var langIcon: String? = null,
)