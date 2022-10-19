package com.example.soiltesting.model.temp

data class HistoryTrackerResponse(
    val `data`: List<Data>,
    val message: String,
    val status: Boolean
)