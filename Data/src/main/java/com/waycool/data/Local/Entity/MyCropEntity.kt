package com.waycool.data.Local.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.waycool.data.Network.NetworkModels.CropModel

@Entity(tableName = "My_crop")

data class MyCropDataEntity (
    @PrimaryKey
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
   // @SerializedName("crop"                  ) var crop                : MyCropEntity   = MyCropEntity()
    @SerializedName("idd"        ) var cropIdd       : Int?    = null,
    @SerializedName("crop_name" ) var cropName : String? = null,
    @SerializedName("crop_logo" ) var cropLogo : String? = null

)
//data class MyCropEntity(
//    @SerializedName("id"        ) var id       : Int?    = null,
//    @SerializedName("crop_name" ) var cropName : String? = null,
//    @SerializedName("crop_logo" ) var cropLogo : String? = null
//
//)
