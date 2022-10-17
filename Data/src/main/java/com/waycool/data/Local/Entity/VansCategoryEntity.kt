package com.waycool.data.Local.Entity

import com.google.gson.annotations.SerializedName

data class VansCategoryEntity(
    var id: Int? = null,
    var categoryName: String? = null,
    var vansType: String? = null,
    var translation: String? = null,
)
