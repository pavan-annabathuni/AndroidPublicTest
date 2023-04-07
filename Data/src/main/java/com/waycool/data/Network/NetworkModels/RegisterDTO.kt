package com.waycool.data.Network.NetworkModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RegisterDTO(@SerializedName("status") var status: Boolean? = null,
                       @SerializedName("message") var message: String? = null,
                       @SerializedName("data") var data: RegisterData)

@Keep
data class RegisterData(@SerializedName("name") var name: String? = null,
                        @SerializedName("contact") var contact: String? = null,
                        @SerializedName("email") var email: String? = null,
                        @SerializedName("approved") var approved: Int? = null,
                        @SerializedName("updated_at") var updatedAt: String? = null,
                        @SerializedName("created_at") var createdAt: String? = null,
                        @SerializedName("id") var id: Int? = null)
