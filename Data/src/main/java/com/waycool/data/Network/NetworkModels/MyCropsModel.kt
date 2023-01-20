package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class MyCropsModel(
    @SerializedName("status"  ) var status  : Boolean?        = null,
                        @SerializedName("message" ) var message : String?         = null,
                        @SerializedName("data"    ) var data    : ArrayList<MyCropDataModel> = arrayListOf())
data class CropModel (

    @SerializedName("id"        ) var id       : Int?    = null,
    @SerializedName("crop_name" ) var cropName : String? = null,
    @SerializedName("crop_name_tag" ) var cropTagName : String? = null,
    @SerializedName("crop_logo" ) var cropLogo : String? = null

)

data class MyCropDataModel (

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
    @SerializedName("crop"                  ) var crop                : CropModel?   = CropModel(),
    @SerializedName("soil_type"             ) var soilType            : SoilType? = SoilType()
)
data class SoilType (

    @SerializedName("id"          ) var id          : Int?    = null,
    @SerializedName("soil_type"   ) var soilType    : String? = null,
    @SerializedName("sat"         ) var sat         : Double?    = null,
    @SerializedName("fc"          ) var fc          : Double?    = null,
    @SerializedName("wp"          ) var wp          : Double?    = null,
    @SerializedName("soil_drain"  ) var soilDrain   : Double?    = null,
    @SerializedName("ksat"        ) var ksat        : Double?    = null,
    @SerializedName("translation" ) var translation : String? = null,
    @SerializedName("created_at"  ) var createdAt   : String? = null,
    @SerializedName("updated_at"  ) var updatedAt   : String? = null,
    @SerializedName("deleted_at"  ) var deletedAt   : String? = null

)