package com.waycool.data.repository.domainModels

import androidx.room.ColumnInfo


data class CropMasterDomain(
    var cropId: Int? = null,
    var cropName: String? = null,
    var cropCategory_id: Int? = null,
    var cropLogo: String? = null,
    var cropType: String? = null,
    var translation: String? = null,
    var cropNameTag: String? = null,
    var isWaterModel:Int?,
    var diseasePrediction: Int? = 0,

    )

