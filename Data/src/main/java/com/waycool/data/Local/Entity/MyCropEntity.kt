package com.waycool.data.Local.Entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "My_crop")
@Keep
data class MyCropDataEntity (
    @PrimaryKey
    @ColumnInfo(name="id"                    ) var id                  : Int?    = null,
    @ColumnInfo(name="plot"                  ) var plot                : String? = null,
    @ColumnInfo(name="plot_nickname"         ) var plotNickname        : String? = null,
    @ColumnInfo(name="plot_json"             ) var plotJson            : String? = null,
    @ColumnInfo(name="is_active"             ) var isActive            : Int?    = null,
    @ColumnInfo(name="crop_shade"            ) var cropShade           : String? = null,
    @ColumnInfo(name="sowing_date"           ) var sowingDate          : String? = null,
    @ColumnInfo(name="irrigation_type"       ) var irrigationType      : String? = null,
    @ColumnInfo(name="no_of_plants"          ) var noOfPlants          : String? = null,
    @ColumnInfo(name="len_drip"              ) var lenDrip             : String? = null,
    @ColumnInfo(name="width_drip"            ) var widthDrip           : String? = null,
    @ColumnInfo(name="area"                  ) var area                : String? = null,
    @ColumnInfo(name="crop_year"             ) var cropYear            : Int?    = null,
    @ColumnInfo(name="crop_season"           ) var cropSeason          : String? = null,
    @ColumnInfo(name="crop_stage"            ) var cropStage           : String? = null,
    @ColumnInfo(name="drip_emitter_rate"     ) var dripEmitterRate     : String? = null,
    @ColumnInfo(name="expected_harvest_date" ) var expectedHarvestDate : String? = null,
    @ColumnInfo(name="actual_harvest_date"   ) var actualHarvestDate   : String? = null,
    @ColumnInfo(name="estimated_yield"       ) var estimatedYield      : String? = null,
    @ColumnInfo(name="actual_yield"          ) var actualYield         : String? = null,
    @ColumnInfo(name="crop_id"               ) var cropId              : Int?    = null,
    @ColumnInfo(name="account_no_id"         ) var accountNoId         : Int?    = null,
    @ColumnInfo(name="farm_id"               ) var farmId              : Int?    = null,
    @ColumnInfo(name="soil_type_id"          ) var soilTypeId          : Int?    = null,
    @ColumnInfo(name="crop_variety_id"       ) var cropVarietyId       : String? = null,
   // @ColumnInfo(name="crop"                  ) var crop                : MyCropEntity   = MyCropEntity()
    @ColumnInfo(name="idd"        ) var cropIdd       : Int?    = null,
    @ColumnInfo(name="crop_name" ) var cropName : String? = null,
    @ColumnInfo(name="crop_name_tag" ) var cropNameTag : String? = null,
    @ColumnInfo(name="crop_logo" ) var cropLogo : String? = null,
    @ColumnInfo(name="soil_type"   ) var soilType    : String? = null,

    @ColumnInfo(name = "irrigation_required"   ) var irrigationRequired  : Boolean?  = null,
    @ColumnInfo(name = "disease"               ) var disease             : Boolean?  = null,

)
//data class MyCropEntity(
//    @SerializedName("id"        ) var id       : Int?    = null,
//    @SerializedName("crop_name" ) var cropName : String? = null,
//    @SerializedName("crop_logo" ) var cropLogo : String? = null
//
//)
