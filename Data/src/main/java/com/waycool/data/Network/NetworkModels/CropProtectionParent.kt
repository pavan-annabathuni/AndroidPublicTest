package com.waycool.data.Network.NetworkModels

import androidx.annotation.Keep

@Keep
class CropProtectionParent(
    var crop_id: Int?,
    var disease_name: String?,
    var disease_id: Int?,
    var thumbImage: String?,
    var imageUrl: String?,
    var audioUrl: String?,
    var mode_of_infestation: String?,
    var symptoms: String?,
    var recommendations: RecommendationNetwork?,
    var childModelList: List<CropProtectionChild>?
)