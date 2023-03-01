package com.waycool.data.repository.domainModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class VarietyCropDomain(
    var id: Int? = null,
    var varietyTag: String? = null,
    var variety: String? = null,
    var cropId: Int? = null,
    var purpose: String? = null,
    val isSelected: Boolean? = null
)
