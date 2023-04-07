package com.waycool.data.repository.domainModels

import androidx.annotation.Keep

@Keep

data class LanguageMasterDomain(
    var id: Int? = null,
    var lang: String? = null,
    var langNative: String? = null,
    var langCode: String? = null,
    var langIcon: String? = null,
)