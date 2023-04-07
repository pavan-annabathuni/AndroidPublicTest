package com.waycool.data.repository.domainModels

import androidx.annotation.Keep

@Keep
data class SoilTestHistoryDomain (
    val id: Int?=null,
    val plot_no: String?=null,
    val soil_test_number: String?=null,
    val status: String?=null,
    val updated_at: String?=null,
    val approve_status:String?=null

        )