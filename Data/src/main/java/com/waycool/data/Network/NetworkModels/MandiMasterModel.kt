package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

class MandiMasterModel (
    @SerializedName("status"  ) var status  : Boolean?        = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("data"    ) var data    : ArrayList<MandiMasterData> = arrayListOf()

)
data class MandiMasterData (

    @SerializedName("id"             ) var id           : Int?    = null,
    @SerializedName("mandi_name"     ) var mandiName    : String? = null,
    @SerializedName("mandi_name_tag" ) var mandiNameTag : String? = null

)