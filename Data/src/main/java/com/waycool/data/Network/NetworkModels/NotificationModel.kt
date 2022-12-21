package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class NotificationModel (
    @SerializedName("status"  ) var status  : Boolean?        = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("data"    ) var data    : ArrayList<DataNotification> = arrayListOf()
        )

data class DataNotification (

    @SerializedName("id"      ) var id     : Int?    = null,
    @SerializedName("type"    ) var type   : String? = null,
    @SerializedName("read_at" ) var readAt : String? = null,
    @SerializedName("data"    ) var data   : Notification   = Notification()

)
data class Notification (
    @SerializedName("title" ) var title : String? = null,
    @SerializedName("body"  ) var body  : String? = null,
    @SerializedName("media" ) var media : String? = null,
    @SerializedName("image" ) var image : String? = null,
    @SerializedName("link"  ) var link  : String? = null

)