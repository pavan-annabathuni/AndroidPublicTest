package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class AddCropResponseDTO (

    @SerializedName("status"  ) var status  : Boolean? = null,
    @SerializedName("message" ) var message : String?  = null,
    @SerializedName("data"    ) var data: AddCropResponseData)

    data class AddCropResponseData(
        @SerializedName("crop_id"           ) var cropId          : Int?    = null,
        @SerializedName("account_no_id"     ) var accountNoId     : Int?    = null,
        @SerializedName("farm_id"           ) var farmId          : String? = null,
        @SerializedName("soil_type_id"      ) var soilTypeId      : String? = null,
        @SerializedName("plot"              ) var plot            : String? = null,
        @SerializedName("plot_nickname"     ) var plotNickname    : String? = null,
        @SerializedName("plot_json"         ) var plotJson        : String? = null,
        @SerializedName("is_active"         ) var isActive        : Int?    = null,
        @SerializedName("crop_variety_id"   ) var cropVarietyId   : String? = null,
        @SerializedName("crop_shade"        ) var cropShade       : String? = null,
        @SerializedName("sowing_date"       ) var sowingDate      : String? = null,
        @SerializedName("irrigation_type"   ) var irrigationType  : String? = null,
        @SerializedName("no_of_plants"      ) var noOfPlants      : String? = null,
        @SerializedName("len_drip"          ) var lenDrip         : String? = null,
        @SerializedName("width_drip"        ) var widthDrip       : String? = null,
        @SerializedName("area"              ) var area            : String? = null,
        @SerializedName("crop_year"         ) var cropYear        : String? = null,
        @SerializedName("crop_season"       ) var cropSeason      : String? = null,
        @SerializedName("crop_stage"        ) var cropStage       : String? = null,
        @SerializedName("drip_emitter_rate" ) var dripEmitterRate : String? = null,
        @SerializedName("id"                ) var id              : Int?    = null
    )