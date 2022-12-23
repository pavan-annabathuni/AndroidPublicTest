package com.waycool.data.repository.domainModels

import com.google.gson.annotations.SerializedName

data class VerifyQrDomain(
   var status  : Boolean? = null,
   var message : String?  = null,
   var data    : String?  = null
)
