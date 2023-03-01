package com.waycool.data.Network.NetworkModels

import androidx.annotation.Keep
import retrofit2.http.Url

@Keep
data class PdfDownloadDTO(
    @Url val url: String
)
