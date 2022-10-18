package com.waycool.data.Local.Entity

import com.google.gson.annotations.SerializedName

data class AddCropTypeEntity(
    val id: Int? = null,
    val isSelected:Boolean?=null,
    val soil_type: String? = null
)
