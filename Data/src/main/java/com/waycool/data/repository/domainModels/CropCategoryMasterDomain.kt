package com.waycool.data.repository.domainModels

import androidx.annotation.Keep

@Keep
data class CropCategoryMasterDomain(
    var id: Int? = null,
    var categoryName: String? = null,
    var translation: String? = null,
    var categoryTagName: String? = null
)
