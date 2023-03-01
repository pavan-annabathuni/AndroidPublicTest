package com.waycool.data.Local.Entity

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class VansCategoryEntity(
    var id: Int? = null,
    var categoryName: String? = null,
    var vansType: String? = null,
    var translation: String? = null,
)
