package com.example.addcrop.model.addcroppost

data class AddCropRequest(
    val account_no_id: Int,
    val area: String,
    val crop_id: Int,
    val crop_season: String,
    val crop_shade: String,
    val crop_stage: String,
    val crop_variety_id: Int,
    val crop_year: Int,
    val drip_emitter_rate: String,
    val farm_id: Int,
    val irrigation_type: String,
    val len_drip: String,
    val no_of_plants: String,
    val plot_json: String,
    val plot_nickname: String,
    val soil_type_id: Int,
    val sowing_date: String,
    val width_drip: String
)