package com.example.mandiprice.api.mandiResponse

data class Data(
    val numberOfRecordsPerPage: Int,
    val page: Int,
    val records: List<Record>,
    val startFrom: Int,
    val total_pages: Int,
    val total_results: Int
)