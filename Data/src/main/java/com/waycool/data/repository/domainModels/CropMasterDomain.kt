package com.waycool.data.repository.domainModels


data class CropMasterDomain(
    var cropId: Int? = null,
    var cropName: String? = null,
    var cropCategory_id: Int? = null,
    var cropLogo: String? = null,
    var cropType: String? = null,
    var translation: String? = null
)

