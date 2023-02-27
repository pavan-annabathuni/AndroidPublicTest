package com.waycool.data.repository.domainModels

import androidx.annotation.Keep

@Keep
data class LogoutDomain(
    var status: Boolean?=null,
    var message: String?=null,
    var data: String?=null
)
