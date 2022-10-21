package com.example.soiltesting.model.postsoil

data class NewSoilTestPost(
    val soil_test_number:String,
    val plot_no: String,
    val pincode: String,
    val lat:String,
    val long:String,
    val address: String,
    val district: String,
    val state: String,
    val contact_number: String,
   val user_id:String
)