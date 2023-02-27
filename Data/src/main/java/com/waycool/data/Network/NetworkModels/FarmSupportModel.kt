package com.waycool.data.Network.NetworkModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class FarmSupportModel(
    @SerializedName("status"  ) var status  : Boolean? = null,
    @SerializedName("message" ) var message : String?  = null,
    @SerializedName("data"    ) var data    : FarmSupportData?    = FarmSupportData()

)

@Keep
data class FarmSupportData (

    @SerializedName("name"     ) var name     : String? = null,
    @SerializedName("contact"  ) var contact  : String? = null,
    @SerializedName("email"    ) var email    : String? = null,
    @SerializedName("approved" ) var approved : Int?    = null,
    @SerializedName("id"       ) var id       : Int?    = null

)

@Keep
data class GetFarmSupport(
    @SerializedName("status"  ) var status  : Boolean?        = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("data"    ) var data    : ArrayList<GetFarmSupportData> = arrayListOf()

)

@Keep
data class GetFarmSupportData (

    @SerializedName("id"                ) var id              : Int?               = null,
    @SerializedName("name"              ) var name            : String?            = null,
    @SerializedName("contact"           ) var contact         : String?            = null,
    @SerializedName("email"             ) var email           : String?            = null,
    @SerializedName("email_verified_at" ) var emailVerifiedAt : String?            = null,
    @SerializedName("approved"          ) var approved        : Int?               = null,
    @SerializedName("settings"          ) var settings        : String?            = null,
    @SerializedName("roles"             ) var roles           : ArrayList<Roles>   = arrayListOf(),
//    @SerializedName("profile"           ) var profile         : Profile?           = Profile(),
//    @SerializedName("account"           ) var account         : ArrayList<Account> = arrayListOf()

)
@Keep
data class Roles (

    @SerializedName("id"          ) var id          : Int?    = null,
    @SerializedName("title"       ) var title       : String? = null,
    @SerializedName("department"  ) var department  : String? = null,
    @SerializedName("translation" ) var translation : String? = null,
    @SerializedName("audio_url"   ) var audioUrl    : String? = null,
    @SerializedName("created_at"  ) var createdAt   : String? = null,
    @SerializedName("updated_at"  ) var updatedAt   : String? = null,
    @SerializedName("deleted_at"  ) var deletedAt   : String? = null,

)

@Keep
data class DeleteFarmSupport(
    @SerializedName("status"  ) var status  : Boolean? = null,
    @SerializedName("message" ) var message : String?  = null,
    @SerializedName("data"    ) var data    : Boolean? = null
)