package com.waycool.data.repository.domainModels

data class AddCropRequestDomain(
    val account_no_id: Int?= null,
    val area: Int ?= null,
    val crop_id: String ?= null,
    val crop_season: String?= null,
    val crop_shade: String?= null,
    val crop_stage: Int? = null,
    val crop_variety_id: String? = null,
    val crop_year: String? = null,
    val drip_emitter_rate: String?= null,
    val farm_id: String? = null,
    val irrigation_type: String?= null,
    val len_drip: String?= null,
    val no_of_plants: String?= null,
    val plot_json: Int ?= null,
    val plot_nickname: String?= null,
    val soil_type_id: String? = null,
    val sowing_date: String?= null,
    val width_drip: String?= null
)
