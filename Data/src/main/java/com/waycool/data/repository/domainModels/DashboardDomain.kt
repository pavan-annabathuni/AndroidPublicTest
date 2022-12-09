package com.waycool.data.repository.domainModels

import com.google.gson.annotations.SerializedName
import com.waycool.data.Network.NetworkModels.DashboardUser

data class DashboardDomain (
        @SerializedName("status"  ) var status  : Boolean? = null,
        @SerializedName("message" ) var message : String?  = null,
        @SerializedName("data"    ) var data    : DashboardDoimans?    = DashboardDoimans()
)
data class DashboardDoimans(
        @SerializedName("id"                ) var id              : Int?              = null,
        @SerializedName("name"              ) var name            : String?           = null,
        @SerializedName("contact"           ) var contact         : String?           = null,
        @SerializedName("email"             ) var email           : String?           = null,
        @SerializedName("email_verified_at" ) var emailVerifiedAt : String?           = null,
        @SerializedName("approved"          ) var approved        : Int?              = null,
        @SerializedName("settings"          ) var settings        : String?           = null,
        @SerializedName("isPremium"         ) var isPremium       : Boolean?          = null,
        @SerializedName("profile"           ) var profile         : String?           = null,
        @SerializedName("account"           ) var account         : ArrayList<String> = arrayListOf()
        )