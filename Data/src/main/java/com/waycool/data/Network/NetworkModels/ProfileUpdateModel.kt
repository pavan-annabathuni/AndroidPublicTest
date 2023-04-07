package com.waycool.data.Network.NetworkModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ProfileUpdateResponseDTO(
    @SerializedName("status"  ) var status  : Boolean? = null,
    @SerializedName("message" ) var message : String?  = null,
    @SerializedName("data"    ) var data    : Data?    = Data()
)

@Keep
data class Data (

    @SerializedName("user"    ) var user    : User?    = User(),
    @SerializedName("profile" ) var profile : Profile? = Profile()

)

@Keep
data class User (

    @SerializedName("name" ) var name : String? = null,
    @SerializedName("id"   ) var id   : Int?    = null

)

@Keep
data class Profile (

    @SerializedName("id"           ) var id          : Int?    = null,
    @SerializedName("gender"       ) var gender      : String? = null,
    @SerializedName("address"      ) var address     : String? = null,
    @SerializedName("village"      ) var village     : String? = null,
    @SerializedName("pincode"      ) var pincode     : String? = null,
    @SerializedName("state"        ) var state       : String? = null,
    @SerializedName("district"     ) var district    : String? = null,
    @SerializedName("sub_district" ) var subDistrict : String? = null,
    @SerializedName("city"         ) var city        : String? = null,
    @SerializedName("country"      ) var country     : String? = null,
    @SerializedName("lat"          ) var lat         : String? = null,
    @SerializedName("long"         ) var long        : String? = null,
    @SerializedName("profile_pic"  ) var profilePic  : String? = null,
    @SerializedName("user_id"      ) var userId      : Int?    = null,
    @SerializedName("lang_id"      ) var langId      : Int?    = null

)