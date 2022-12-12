package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class GraphsViewDataDTO(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: GraphViewData? = GraphViewData()
)

data class GraphViewData(
    @SerializedName("12_hours_data") var LastTodayData: LastTodayData? = LastTodayData(),
    @SerializedName("7_days_data") var SevenDaysData: SevenDaysData? = SevenDaysData()
)

data class LastTodayData(

    @SerializedName("data_timestamp") var dataTimestamp: ArrayList<String> = arrayListOf(),
    @SerializedName("pressure") var pressure: ArrayList<String> = arrayListOf()
)

data class SevenDaysData(
    @SerializedName("data_timestamp") var dataTimestamp: ArrayList<String> = arrayListOf(),
    @SerializedName("pressure") var pressure: ArrayList<String> = arrayListOf()

)

