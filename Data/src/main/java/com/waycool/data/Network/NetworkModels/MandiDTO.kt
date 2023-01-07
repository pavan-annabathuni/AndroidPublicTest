package com.waycool.data.Network.NetworkModels

class MandiDTO(
    val data: MandiData,
    val message: String,
    val status: String
)

data class MandiData(
    val numberOfRecordsPerPage: Int?,
    val page: Int?,
    val records: List<MandiRecordNetwork>,
    val startFrom: Int?,
    val total_pages: Int?,
    val total_results: Int?
)

data class MandiRecordNetwork(
    val arrival_date: String?,
    val avg_price: Double?,
    val created_at: String?,
    val crop: String?,
    val crop_category: String?,
    val crop_logo: String?,
    val crop_master_id: Int?,
    val district: String?,
    val id: String,
    val last_price: Double?,
    val location: String?,
    val mandi_master_id: Int?,
    val market: String?,
    val max_price: Double?,
    val min_price: Double?,
    val modal_price: Double?,
    val price_diff: Double?,
    val price_status: Int?,
    val source: String?,
    val state: String?,
    val sub_district: String?,
    val updated_at: String?,
    val variety: String?,
    val distance: Double?
)