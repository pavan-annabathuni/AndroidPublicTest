package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class AppTranlationsDTO (
    @SerializedName("status"  ) var status  : Boolean?        = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("data"    ) var data    : ArrayList<AppTranslationsNetwork> = arrayListOf()

)

data class AppTranslationsNetwork (

    @SerializedName("id"            ) var id          : Int?    = null,
    @SerializedName("app_key"       ) var appKey      : String? = null,
    @SerializedName("app_value"     ) var appValue    : String? = null,
    @SerializedName("created_at"    ) var createdAt   : String? = null,
    @SerializedName("app_value_tag" ) var appValueTag : String? = null

)