package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class PestDiseaseDTO(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: ArrayList<PestDiseaseData> = arrayListOf()
)
data class PestDiseaseData(
    @SerializedName("crop_id") var cropId: Int? = null,
    @SerializedName("disease_name") var diseaseName: String? = null,
    @SerializedName("disease_id") var diseaseId: Int? = null,
    @SerializedName("thumb") var thumb: String? = null,
    @SerializedName("image_url") var imageUrl: String? = null,
    @SerializedName("audio_url") var audioUrl: String? = null,
    @SerializedName("mode_of_infestation") var modeOfInfestation: String? = null,
    @SerializedName("symptoms") var symptoms: String? = null,
    @SerializedName("recommendation") var recommendation: RecommendationNetwork? = null
)
//data class Symptoms(
//    @SerializedName("image_url") var imageUrl: String? = null,
//    @SerializedName("is_active") var isActive: Int? = null,
//    @SerializedName("symptoms") var symptoms: String? = null,
//    @SerializedName("audio_url") var audioUrl: String? = null,
//    @SerializedName("translation") var translation: String? = null,
//)
data class RecommendationNetwork(
    @SerializedName("cultural") var cultural: String? = null,
    @SerializedName("biological") var biological: String? = null,
    @SerializedName("chemical") var chemical: String? = null,
)
