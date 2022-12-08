package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class FarmSupportModel(
    @SerializedName("status"  ) var status  : Boolean? = null,
    @SerializedName("message" ) var message : String?  = null,
    @SerializedName("data"    ) var data    : FarmSupportData?    = FarmSupportData()

)

data class FarmSupportData (

    @SerializedName("name"     ) var name     : String? = null,
    @SerializedName("contact"  ) var contact  : String? = null,
    @SerializedName("email"    ) var email    : String? = null,
    @SerializedName("approved" ) var approved : Int?    = null,
    @SerializedName("id"       ) var id       : Int?    = null

)