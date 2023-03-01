package com.waycool.data.repository.domainModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class VerifyQrDomain(
   var status  : Boolean? = null,
   var message : String?  = null,
   var data    : String?  = null
)
