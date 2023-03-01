package com.waycool.data.Network.NetworkModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class UserDetailsDTO(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: UserDetailsData? = UserDetailsData()

)

@Keep
data class UserDetailsData(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("phone") var contact: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("jwt"     ) var jwt     : String?            = null,
    @SerializedName("profile") var profile: ProfileNetwork? = ProfileNetwork(),
    @SerializedName("account") var account: List<AccountNetwork> = arrayListOf(),
    @SerializedName("roles"   ) var roles   : ArrayList<RolesNetwork>   = arrayListOf()

)

@Keep
data class RolesNetwork (

    @SerializedName("title" ) var title : String? = null,
    @SerializedName("pivot" ) var pivot : PivotNetwork?  = PivotNetwork()

)
@Keep
data class PivotNetwork (

    @SerializedName("user_id" ) var userId : Int? = null,
    @SerializedName("role_id" ) var roleId : Int? = null

)
@Keep
data class AccountNetwork(

    @SerializedName("id"              ) var id             : Int?    = null,
    @SerializedName("account_no"      ) var accountNo      : String? = null,
    @SerializedName("account_type"    ) var accountType    : String? = null,
    @SerializedName("is_active"       ) var isActive       : Int?    = null,
    @SerializedName("default_modules" ) var defaultModules : Int?    = null,
    @SerializedName("subscription"    ) var subscription   : Int?    = null
)

@Keep
data class ProfileNetwork(
    @SerializedName("id"               ) var id             : Int?    = null,
    @SerializedName("remote_photo_url" ) var remotePhotoUrl : String? = null,
    @SerializedName("lang_id"          ) var langId         : Int?    = null,
    @SerializedName("user_id"          ) var userId         : Int?    = null,
    @SerializedName("lat"              ) var lat            : String? = null,
    @SerializedName("long"             ) var long           : String? = null,
    @SerializedName("pincode"          ) var pincode        : String? = null,
    @SerializedName("village"          ) var village        : String? = null,
    @SerializedName("address"          ) var address        : String? = null,
    @SerializedName("state"            ) var state          : String? = null,
    @SerializedName("district"         ) var district       : String? = null,
    @SerializedName("sub_district"     ) var subDistrict    : String? = null,
    @SerializedName("lang"             ) var lang           : Lang?   = Lang()

)
@Keep
data class Lang (

    @SerializedName("id"        ) var id       : Int?    = null,
    @SerializedName("lang_code" ) var langCode : String? = null,
    @SerializedName("lang"      ) var lang     : String? = null

)