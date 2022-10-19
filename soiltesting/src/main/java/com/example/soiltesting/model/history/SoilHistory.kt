package com.example.soiltesting.model.history

data class SoilHistory(
    val `data`: List<Data>,
    val message: String,
    val status: Boolean
)