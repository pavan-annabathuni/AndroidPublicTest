package com.waycool.data.repository.domainModels

import androidx.annotation.Keep

@Keep
data class SoilTypeDomain(
    val id: Int? = null,
    val isSelected:Boolean?=null,
    val soil_type: String? = null
)
