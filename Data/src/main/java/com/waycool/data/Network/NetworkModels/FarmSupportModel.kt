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

data class GetFarmSupport(
    @SerializedName("status"  ) var status  : Boolean?        = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("data"    ) var data    : ArrayList<GetFarmSupportData> = arrayListOf()

)
data class GetFarmSupportData (

    @SerializedName("id"                ) var id              : Int?               = null,
    @SerializedName("name"              ) var name            : String?            = null,
    @SerializedName("contact"           ) var contact         : String?            = null,
    @SerializedName("email"             ) var email           : String?            = null,
    @SerializedName("email_verified_at" ) var emailVerifiedAt : String?            = null,
    @SerializedName("approved"          ) var approved        : Int?               = null,
    @SerializedName("settings"          ) var settings        : String?            = null,
//    @SerializedName("roles"             ) var roles           : ArrayList<Roles>   = arrayListOf(),
//    @SerializedName("profile"           ) var profile         : Profile?           = Profile(),
//    @SerializedName("account"           ) var account         : ArrayList<Account> = arrayListOf()

)

data class DeleteFarmSupport(
    @SerializedName("status"  ) var status  : Boolean? = null,
    @SerializedName("message" ) var message : String?  = null,
    @SerializedName("data"    ) var data    : Boolean? = null
)