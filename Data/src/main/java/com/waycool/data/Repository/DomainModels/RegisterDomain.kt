package com.waycool.data.Repository.DomainModels

import com.google.gson.annotations.SerializedName

data class RegisterDomain(
    var name: String? = null,
    var contact: String? = null,
    var email: String? = null,
    var approved: Int? = null,
    var id: Int? = null
)

