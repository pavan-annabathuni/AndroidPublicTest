package com.example.addcrop.model

data class AddCropResponse(
    val `data`: List<Data>,
    val message: String,
    val status: Boolean
)