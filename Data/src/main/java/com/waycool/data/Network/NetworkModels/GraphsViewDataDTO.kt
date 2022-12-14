package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class GraphsViewDataDTO(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: GraphViewData? = GraphViewData()
)

data class GraphViewData(
    @SerializedName("12_hours_data" ) var LastTodayData : LastTodayData? = LastTodayData(),
    @SerializedName("30_days_data"  ) var MonthDaysData  : MonthDaysData?  = MonthDaysData()

)

data class LastTodayData(

    @SerializedName("15 PM" ) var one : String? = null,
    @SerializedName("14 PM" ) var two : String? = null,
    @SerializedName("13 PM" ) var three : String? = null,
    @SerializedName("12 PM" ) var four : String? = null,
    @SerializedName("10 AM" ) var five : String? = null,
    @SerializedName("09 AM" ) var six : String? = null,
    @SerializedName("08 AM" ) var seven: String? = null,
    @SerializedName("07 AM" ) var eighet: String? = null,
    @SerializedName("05 AM" ) var nine : String? = null,
    @SerializedName("03 AM" ) var ten : String? = null

)

data class MonthDaysData(
    @SerializedName("13 Dec" ) var tone : String? = null,
    @SerializedName("12 Dec" ) var tTwo : String? = null,
    @SerializedName("11 Dec" ) var tThree : String? = null,
    @SerializedName("10 Dec" ) var tFour : String? = null,
    @SerializedName("09 Dec" ) var tFive : String? = null,
    @SerializedName("08 Dec" ) var tSix : String? = null

)



