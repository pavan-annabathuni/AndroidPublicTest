package com.waycool.data.repository.domainModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class MandiHistoryDomain(
    @SerializedName("status"  ) var status  : Boolean?        = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("data"    ) var data    : ArrayList<MandiHistoryDataDomain> = arrayListOf()

)
@Keep
data class MandiHistoryDataDomain (

    @SerializedName("avg_price") var avgPrice    : String? = null,
    @SerializedName("arrival_date") var arrivalDate : String? = null,
    @SerializedName("updated_at"           ) var updatedAt          : String? = null,
    @SerializedName("formatted_updated_at" ) var formattedUpdatedAt : String? = null


)
