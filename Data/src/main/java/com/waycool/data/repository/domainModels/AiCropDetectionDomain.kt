package com.waycool.data.repository.domainModels

import androidx.annotation.Keep

@Keep
data class AiCropDetectionDomain(
    var id: Int? = null,
    var imageUrl: String? = null,
    var prediction: String? = null,
    var probability: String? = null,
    var cropId: String? = null,
    var diseaseId: Int? = null,
    var message:String?=null,
    var status:Boolean?=null
)
