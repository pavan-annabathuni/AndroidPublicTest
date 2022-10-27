package com.waycool.data.repository.domainModels

import com.google.gson.annotations.SerializedName

data class MandiHistoryDomain(
    @SerializedName("status"  ) var status  : Boolean?        = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("data"    ) var data    : ArrayList<MandiHistoryDataDomain> = arrayListOf()

)

data class MandiHistoryDataDomain (

    @SerializedName("avg_price") var avgPrice    : String? = null,
    @SerializedName("arrival_date") var arrivalDate : String? = null

)
