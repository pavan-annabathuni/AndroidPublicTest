package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class CheckTokenResponseDTO(
    @SerializedName("status"  ) var status  : Boolean? = null,
    @SerializedName("message" ) var message : String?  = null,
//    @SerializedName("data"    ) var data    : CheckTokenResponse
    )


//data class CheckTokenResponse(
//    @SerializedName("id"             ) var id            : Int?              = null,
//    @SerializedName("tokenable_type" ) var tokenableType : String?           = null,
//    @SerializedName("tokenable_id"   ) var tokenableId   : Int?              = null,
//    @SerializedName("name"           ) var name          : String?           = null,
//    @SerializedName("abilities"      ) var abilities     : ArrayList<String> = arrayListOf(),
//    @SerializedName("last_used_at"   ) var lastUsedAt    : String?           = null,
//    @SerializedName("created_at"     ) var createdAt     : String?           = null,
//    @SerializedName("updated_at"     ) var updatedAt     : String?           = null
//
//)