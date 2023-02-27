package com.waycool.data.Network.NetworkModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class MandiMasterModel (
    @SerializedName("status"  ) var status  : Boolean?        = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("data"    ) var data    : ArrayList<MandiMasterData> = arrayListOf()

)
@Keep
data class MandiMasterData (

    @SerializedName("id"             ) var id           : Int?    = null,
    @SerializedName("mandi_name"     ) var mandiName    : String? = null,
    @SerializedName("mandi_name_tag" ) var mandiNameTag : String? = null

)