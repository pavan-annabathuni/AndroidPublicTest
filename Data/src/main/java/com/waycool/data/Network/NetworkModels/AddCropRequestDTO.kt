package com.waycool.data.Network.NetworkModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class AddCropRequestDTO(
    @SerializedName("crop_id"               ) var cropId              : Int?    = null,
    @SerializedName("account_no_id"         ) var accountNoId         : Int?    = null,
    @SerializedName("plot"                  ) var plot                : String? = null,
    @SerializedName("plot_nickname"         ) var plotNickname        : String? = null,
    @SerializedName("plot_json"             ) var plotJson            : String? = null,
    @SerializedName("is_active"             ) var isActive            : Int?    = null,
    @SerializedName("crop_shade"            ) var cropShade           : String? = null,
    @SerializedName("sowing_date"           ) var sowingDate          : String? = null,
    @SerializedName("irrigation_type"       ) var irrigationType      : String? = null,
    @SerializedName("no_of_plants"          ) var noOfPlants          : String? = null,
    @SerializedName("len_drip"              ) var lenDrip             : String? = null,
    @SerializedName("width_drip"            ) var widthDrip           : String? = null,
    @SerializedName("area"                  ) var area                : String? = null,
    @SerializedName("crop_year"             ) var cropYear            : Int?    = null,
    @SerializedName("crop_season"           ) var cropSeason          : String? = null,
    @SerializedName("crop_stage"            ) var cropStage           : String? = null,
    @SerializedName("drip_emitter_rate"     ) var dripEmitterRate     : String? = null,
    @SerializedName("expected_harvest_date" ) var expectedHarvestDate : String? = null,
    @SerializedName("actual_harvest_date"   ) var actualHarvestDate   : String? = null,
    @SerializedName("estimated_yield"       ) var estimatedYield      : String? = null,
    @SerializedName("actual_yield"          ) var actualYield         : String? = null
)
