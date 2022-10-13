package com.waycool.data.Repository.DomainModels

import com.google.gson.annotations.SerializedName

data class LanguageMasterDomain(
    var id: Int? = null,
    var lang: String? = null,
    var langNative: String? = null,
    var langCode: String? = null,
    var langIcon: String? = null,
)