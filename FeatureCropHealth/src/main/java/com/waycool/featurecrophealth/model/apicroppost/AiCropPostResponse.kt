package com.waycool.featurecrophealth.model.apicroppost

import com.google.gson.annotations.SerializedName

data class AiCropPostResponse(

    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: Data? = Data()
//    val crop_id: Int,
//    val crop_name: String,
//    val image: Image,
//    val user_id: Int
)

data class Data(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("image_url") var imageUrl: String? = null,
    @SerializedName("prediction") var prediction: String? = null,
    @SerializedName("probability") var probability: String? = null,
    @SerializedName("crop_id") var cropId: String? = null,
    @SerializedName("disease_id") var diseaseId: Int? = null,
    @SerializedName("updated_at") var updatedAt: String? = null

)