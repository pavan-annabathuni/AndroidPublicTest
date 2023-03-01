package com.waycool.data.Network.NetworkModels

import androidx.annotation.Keep

@Keep
class CropProtectNewsModel(
    var newsTitle: String,
    var newsdate: String,
    var newsimage: String,
    var newsContent: String,
    var audioUrl: String
)