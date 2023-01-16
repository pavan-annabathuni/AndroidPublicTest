package com.waycool.data.repository.domainModels



data class MandiDomain(
    var status: Boolean? = null,
    var message: String? = null,
    var data: MandiDomainData? = MandiDomainData()
)

data class MandiDomainData(
    var page: Int? = null,
    var numberOfRecordsPerPage: Int? = null,
    var startFrom: Int? = null,
    var total_pages: Int? = null,
    var total_results: Int? = null,
    var records: List<MandiDomainRecord> = emptyList()
)

data class MandiDomainRecord(
    var crop_te: String? = null,
    var market_te: String? = null,
    var crop_master_id: Int? = null,
    var crop_logo: String? = null,
    var market_ta: String? = null,
    var avg_price: Double? = null,
    val distance: Double? = null,
    var source: String? = null,
    var market: String? = null,
    var market_kn: String? = null,
    var crop_hi: String? = null,
    var price_status: Int? = null,
    var crop_kn: String? = null,
    var market_hi: String? = null,
    var crop_mr: String? = null,
    var mandi_master_id: Int? = null,
    var sub_record_id: String? = null,
    var crop_ta: String? = null,
    var crop: String? = null,
    var market_mr: String? = null,
    var id: String? = null
)
