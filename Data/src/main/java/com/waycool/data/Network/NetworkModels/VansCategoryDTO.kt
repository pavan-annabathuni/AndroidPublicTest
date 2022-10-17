package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class VansCategoryDTO(

    @SerializedName("data") var data: ArrayList<VansCategoryData> = arrayListOf()

)

data class VansCategoryData(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("category_name") var categoryName: String? = null,
    @SerializedName("vans_type") var vansType: String? = null,
    @SerializedName("translation") var translation: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null

)