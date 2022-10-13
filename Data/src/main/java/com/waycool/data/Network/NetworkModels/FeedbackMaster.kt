package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class FeedbackMaster(
    @SerializedName("status") var status: Boolean,
    @SerializedName("message") var message: String,
    @SerializedName("data") var data: Feedback
)
data class Feedback(
    @SerializedName("user_id") var userId: Int? = null,
    @SerializedName("feedback") var feedback: String? = null,
    @SerializedName("module") var module: String? = null,
    @SerializedName("screen") var screen: String? = null,
    @SerializedName("desc") var desc: String? = null,
    @SerializedName("id") var id: Int? = null,
)