package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class DashBoardDTO (
    @SerializedName("status"  ) var status  : Boolean? = null,
    @SerializedName("message" ) var message : String?  = null,
    @SerializedName("data"    ) var data    : DashboardNetwork?    = DashboardNetwork()
)
data class DashboardNetwork(
    @SerializedName("id"                ) var id              : Int?              = null,
    @SerializedName("name"              ) var name            : String?           = null,
    @SerializedName("contact"           ) var contact         : String?           = null,
    @SerializedName("email"             ) var email           : String?           = null,
    @SerializedName("email_verified_at" ) var emailVerifiedAt : String?           = null,
    @SerializedName("approved"          ) var approved        : Int?              = null,
    @SerializedName("settings"          ) var settings        : String?           = null,
    @SerializedName("subscription"      ) var subscription    : SubscriptionNetwork?     = SubscriptionNetwork(),
    @SerializedName("profile"           ) var profile         : ProfileNetwork?           = null,
    @SerializedName("account"           ) var account         : ArrayList<AccountNetwork> = arrayListOf()
)
data class SubscriptionNetwork (

    @SerializedName("iot" ) var iot : Boolean? = null

)