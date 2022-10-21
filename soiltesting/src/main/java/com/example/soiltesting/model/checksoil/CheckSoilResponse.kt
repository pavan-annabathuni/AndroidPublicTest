package com.example.soiltesting.model.checksoil

data class CheckSoilResponse(
    val `data`: List<Data>,
    val message: String,
    val status: Boolean
)