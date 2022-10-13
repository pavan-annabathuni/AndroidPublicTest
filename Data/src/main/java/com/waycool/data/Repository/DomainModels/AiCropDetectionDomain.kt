package com.waycool.data.Repository.DomainModels

import com.google.gson.annotations.SerializedName

data class AiCropDetectionDomain(
    var id: Int? = null,
    var imageUrl: String? = null,
    var prediction: String? = null,
    var probability: String? = null,
    var cropId: String? = null,
    var diseaseId: Int? = null
)
