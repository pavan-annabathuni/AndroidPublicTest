package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList

data class SoilTestReportMaster(
    @SerializedName("status") var status: Boolean,
    @SerializedName("message") var message: String,
    @SerializedName("data") var data: ArrayList<ReportDetails> = arrayListOf()
)


data class ReportDetails(
    @SerializedName("id") var id: Int,
    @SerializedName("Tag") var tag: String,
    @SerializedName("request_id") var requestId: String,
    @SerializedName("soil_test_latitude") var StLatitude: String,
    @SerializedName("soil_test_longitude") var StLongitude: String,
    @SerializedName("soil_test_plot_no") var StPlotNo: String,
    @SerializedName("soil_test_sampling_date") var StSamplingdDate: String,
    @SerializedName("farmer_name") var FdName: String,
    @SerializedName("farmer_contact") var FdNumber: String,
    @SerializedName("farmer_address") var FdAddress: String,
    @SerializedName("farmer_sub_district") var FdSubDistrict: String,
    @SerializedName("farmer_district") var FdDistrict: String,
    @SerializedName("farmer_state") var FdState: String,
    @SerializedName("farmer_country") var FdPincode: String,
    @SerializedName("test_center_name") var TcName: String,
    @SerializedName("test_center_village") var TcVillage: String,
    @SerializedName("test_center_address") var TcAddress: String,
    @SerializedName("test_center_district") var TcDistrict: String,
    @SerializedName("test_center_state") var TcState: String,
    @SerializedName("test_center_country") var TcCountry: String,
    @SerializedName("test_center_latitude") var TcLatitude: String,
    @SerializedName("test_center_longitude") var TcLongitude: String,
    @SerializedName("result") var results: Result,
    @SerializedName("crop_name")var crop_name:String,
    @SerializedName("soil_test_testing_date")var soil_test_testing_date:String,


)

data class Result(
    @SerializedName("results") var results: ResultData,
    @SerializedName("reportData") var reportData: ReportData
)

data class ResultData(
    @SerializedName("B") var B: Float,
    @SerializedName("S") var S: Float,
    @SerializedName("k") var k: Float,
    @SerializedName("n") var n: Float,
    @SerializedName("p") var p: Float,
    @SerializedName("Cu") var Cu: Float,
    @SerializedName("EC") var EC: Float,
    @SerializedName("Fe") var Fe: Float,
    @SerializedName("OC") var OC: Float,
    @SerializedName("Zn") var Zn: Float,
    @SerializedName("pH") var pH: Float

)

data class ReportData(
    @SerializedName("parameterInfos") var parameterInfos: ArrayList<ReportResult> = arrayListOf(),
    @SerializedName("ferilizerRecomendations") var ferilizerRecomendations: ArrayList<Recommendation> = arrayListOf()
)

data class Recommendation(
    @SerializedName("fertilizer") var fertilizer: String,
    @SerializedName("quantity") var quantity: String,
    @SerializedName("category") var category: String,
)

data class ReportResult(
    @SerializedName("name") var name: String,
    @SerializedName("key") var key: String,
    @SerializedName("unit") var unit: String,
    @SerializedName("idealRange") var idealRange: String,
    @SerializedName("value") var value: String,
    @SerializedName("recommendation") var recommendation: String,
    @SerializedName("rating") var rating: String,
    @SerializedName("Nutrient Recommendation (kg/ha)") var nutrient: String,
)

data class SoilTestResult(
    @SerializedName("status") var status: Boolean,
    @SerializedName("message") var message: String,
    @SerializedName("data") var data: Objects? = null
)
