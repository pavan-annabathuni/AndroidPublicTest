package com.waycool.data.repository.domainModels

import com.google.gson.annotations.SerializedName

data class MyCropsDomain(@SerializedName("status"  ) var status  : Boolean?        = null,
                         @SerializedName("message" ) var message : String?         = null,
                         @SerializedName("data"    ) var data    : ArrayList<MyCropDataDomain> = arrayListOf()
)

//data class CropDomain (
//
//    @SerializedName("id"        ) var id       : Int?    = null,
//    @SerializedName("crop_name" ) var cropName : String? = null,
//    @SerializedName("crop_logo" ) var cropLogo : String? = null
//
//)

data class MyCropDataDomain (

    @SerializedName("id"                    ) var id                  : Int?    = null,
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
    @SerializedName("actual_yield"          ) var actualYield         : String? = null,
    @SerializedName("crop_id"               ) var cropId              : Int?    = null,
    @SerializedName("account_no_id"         ) var accountNoId         : Int?    = null,
    @SerializedName("farm_id"               ) var farmId              : Int?    = null,
    @SerializedName("soil_type_id"          ) var soilTypeId          : Int?    = null,
    @SerializedName("crop_variety_id"       ) var cropVarietyId       : String? = null,

    @SerializedName("irrigation_required"   ) var irrigationRequired  : Int?  = null,
    @SerializedName("disease"               ) var disease             : Int?  = null,
  //  @SerializedName("crop"                  ) var crop                : CropDomain?   = CropDomain()
    @SerializedName("crop_idd"        ) var idd       : Int?    = null,
    @SerializedName("crop_name" ) var cropName : String? = null,
    @SerializedName("crop_logo" ) var cropLogo : String? = null,
    @SerializedName("soil_type"   ) var soilType    : String? = null,



)