package com.waycool.data.Local.Entity

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class CropCategoryEntity(
    var id: Int? = null,
    var categoryName: String? = null,
    var translation: String? = null,
    val categoryTagName:String?=null
)