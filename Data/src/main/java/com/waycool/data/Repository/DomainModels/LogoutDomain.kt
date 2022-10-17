package com.waycool.data.Repository.DomainModels

import com.google.gson.annotations.SerializedName

data class LogoutDomain(
    var status: Boolean?=null,
    var message: String?=null,
    var data: String?=null
)
