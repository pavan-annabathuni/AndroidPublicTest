package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class SoilTestResponseDTO(
    @SerializedName("status"  ) var status  : Boolean? = null,
    @SerializedName("message" ) var message : String?  = null,
    @SerializedName("data") var data: NewSoilTestResponse)

data class NewSoilTestResponse(
    @SerializedName("soil_test_number" ) var soilTestNumber : String? = null,
    @SerializedName("lat"              ) var lat            : String?    = null,
    @SerializedName("long"             ) var long           : String?    = null,
    @SerializedName("org_id"           ) var orgId          : String? = null,
    @SerializedName("plot_no"          ) var plotNo         : String? = null,
    @SerializedName("status"           ) var status         : Int?    = null,
    @SerializedName("pincode"          ) var pincode        : String? = null,
    @SerializedName("address"          ) var address        : String? = null,
    @SerializedName("number"           ) var number         : String? = null,
    @SerializedName("account_id"       ) var accountId      : String? = null,
    @SerializedName("sub_district"     ) var subDistrict    : String? = null,
    @SerializedName("district"         ) var district       : String? = null,
    @SerializedName("state"            ) var state          : String? = null,
    @SerializedName("user_id"          ) var userId         : Int?    = null,
    @SerializedName("updated_at"       ) var updatedAt      : String? = null,
    @SerializedName("id"               ) var id             : Int?    = null
)
