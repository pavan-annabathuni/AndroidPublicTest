package com.waycool.data.Network.NetworkModels

import retrofit2.http.Url

data class PdfDownloadDTO(
    @Url val url: String
)
