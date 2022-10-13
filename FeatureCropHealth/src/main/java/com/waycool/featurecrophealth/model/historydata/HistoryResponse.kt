package com.waycool.featurecrophealth.model.historydata

data class HistoryResponse(
    val `data`: List<Data>,
    val message: String,
    val status: Boolean
)