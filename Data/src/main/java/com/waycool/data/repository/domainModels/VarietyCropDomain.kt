package com.waycool.data.repository.domainModels

import com.google.gson.annotations.SerializedName

data class VarietyCropDomain(
    var id: Int? = null,
    var varietyTag: String? = null,
    var variety: String? = null,
    var cropId: Int? = null,
    var purpose: String? = null,
)
