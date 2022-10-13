package com.waycool.data.Repository.DomainModels

import com.google.gson.annotations.SerializedName

data class ModuleMasterDomain(
    var id: Int? = null,
    var moduleType: String? = null,
    var moduleIcon: String? = null,
    var moduleDesc: String? = null,
    var Premium: Int? = null,
    var tittle: String? = null,
    var price: Int? = null,
    var audioURl: String? = null,
    var translation: String? = null,
)

