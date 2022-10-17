package com.waycool.data.Repository.DomainModels

import com.google.gson.annotations.SerializedName


data class CropMasterDomain(
    var cropId: Int? = null,
    var cropName: String? = null,
    var cropCategory_id: Int? = null,
    var cropLogo: String? = null,
    var cropType: String? = null,
    var translation: String? = null
)

