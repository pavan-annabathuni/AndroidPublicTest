package com.example.cropinformation.apiservice.response

data class DataX(
    val audio_url: Any,
    val category_id: Int,
    val content_date: String,
    val content_url: String,
    val desc: String,
    val start_date:String,
    val id: Int,
    val is_active: Int,
    val lang_id: Int,
    val module_id: Int,
    val role_id: Int,
    val tags: List<Any>,
    val title: String,
    val tumb_url: String,
    val thumbnail_url: String,
    val updated_at: String,
    val vans_type: String
)