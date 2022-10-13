package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class ModuleMasterDTO(@SerializedName("status") var status: Boolean? = null,
                           @SerializedName("message") var message: String? = null,
                           @SerializedName("data") var data: ArrayList<ModuleData> = arrayListOf())

data class ModuleData(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("module_type") var moduleType: String? = null,
    @SerializedName("module_icon") var moduleIcon: String? = null,
    @SerializedName("module_desc") var moduleDesc: String? = null,
    @SerializedName("is_premium") var Premium: Int? = null,
    @SerializedName("title") var tittle: String? = null,
    @SerializedName("price") var price: Int? = null,
    @SerializedName("audio_url") var audioURl: String? = null,
    @SerializedName("translation") var translation: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null
)
