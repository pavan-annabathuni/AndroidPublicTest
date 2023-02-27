package com.waycool.data.repository.domainModels

import androidx.annotation.Keep

@Keep
data class RegisterDomain(
    var name: String? = null,
    var contact: String? = null,
    var email: String? = null,
    var approved: Int? = null,
    var id: Int? = null
)

