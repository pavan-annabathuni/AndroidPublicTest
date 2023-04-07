package com.waycool.data.repository.domainModels

import androidx.annotation.Keep

@Keep
data class LoginDomain(
    var status: Boolean?=null,
    var message: String?=null,
    var data: String?=null
)
