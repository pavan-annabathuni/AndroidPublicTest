package com.waycool.data.Network.NetworkModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class NotificationModel (
    @SerializedName("status"  ) var status  : Boolean?        = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("data"    ) var data    : ArrayList<DataNotification> = arrayListOf()
        )
@Keep
data class DataNotification (

    @SerializedName("id"      ) var id     : String?    = null,
    @SerializedName("type"    ) var type   : String? = null,
    @SerializedName("read_at" ) var readAt : String? = null,
    @SerializedName("data"    ) var data2   : Notification   = Notification(),
    @SerializedName("date" ) var createdAt : String? = null

)
@Keep
data class Notification (
    @SerializedName("title" ) var title : String? = null,
    @SerializedName("body"  ) var body  : String? = null,
    @SerializedName("media" ) var media : String? = null,
    @SerializedName("image" ) var image : String? = null,
    @SerializedName("link"  ) var link  : String? = null
)
@Keep
data class UpdateNotification(
    @SerializedName("status"  ) var status  : Boolean? = null,
    @SerializedName("message" ) var message : String?  = null,
    @SerializedName("data"    ) var data    : Int?     = null
)