package com.waycool.data.Local.Entity

import com.google.gson.annotations.SerializedName

data class LanguageMasterEntity(
    var id: Int? = null,
    var lang: String? = null,
    var langNative: String? = null,
    var langCode: String? = null,
    var langIcon: String? = null,
)