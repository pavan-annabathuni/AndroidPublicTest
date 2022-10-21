package com.example.cropinformation.apiservice.response.cropAdvisory

data class CropInformation(
    val `data`: List<Data>,
    val message: String,
    val status: Boolean
)