package com.waycool.data.repository.domainModels

import androidx.annotation.Keep

@Keep
data class OTPResponseDomain(
    var message: String? = null,
    var type: String? = null
)