package com.waycool.data.Repository.DomainModels

import com.google.gson.annotations.SerializedName

data class CropCategoryMasterDomain(
    var id: Int? = null,
    var categoryName: String? = null,
    var translation: String? = null,
)
