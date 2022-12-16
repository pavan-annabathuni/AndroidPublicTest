package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class CropStageModel(
    @SerializedName("status"  ) var status  : Boolean? = null,
    @SerializedName("message" ) var message : String?  = null,
    @SerializedName("data"    ) var data    : CropStageData?    = CropStageData()
)
data class CropStageData (

    @SerializedName("account_no_id"                         ) var accountNoId                        : String? = null,
    @SerializedName("farm_id"                               ) var farmId                             : String? = null,
    @SerializedName("plot_id"                               ) var plotId                             : String? = null,
    @SerializedName("fruit_pruning_fruit_pruning"           ) var fruitPruningFruitPruning           : String? = null,
    @SerializedName("fruit_pruning_bud_break"               ) var fruitPruningBudBreak               : String? = null,
    @SerializedName("fruit_pruning_removal_of_excessive"    ) var fruitPruningRemovalOfExcessive     : String? = null,
    @SerializedName("fruit_pruning_shoot_development"       ) var fruitPruningShootDevelopment       : String? = null,
    @SerializedName("fruit_pruning_flowering"               ) var fruitPruningFlowering              : String? = null,
    @SerializedName("fruit_pruning_fruit_set"               ) var fruitPruningFruitSet               : String? = null,
    @SerializedName("fruit_pruning_berry_development"       ) var fruitPruningBerryDevelopment       : String? = null,
    @SerializedName("fruit_pruning_beginning_of_veraison"   ) var fruitPruningBeginningOfVeraison    : String? = null,
    @SerializedName("fruit_pruning_harvest"                 ) var fruitPruningHarvest                : String? = null,
    @SerializedName("fruit_pruning_rest_period"             ) var fruitPruningRestPeriod             : String? = null,
    @SerializedName("foundation_pruning_foundation_pruning" ) var foundationPruningFoundationPruning : String? = null,
    @SerializedName("foundation_pruning_bud_break"          ) var foundationPruningBudBreak          : String? = null,
    @SerializedName("foundation_pruning_cane_thinning"      ) var foundationPruningCaneThinning      : String? = null,
    @SerializedName("foundation_pruning_sub_cane"           ) var foundationPruningSubCane           : String? = null,
    @SerializedName("foundation_pruning_topping"            ) var foundationPruningTopping           : String? = null,
    @SerializedName("created_at"                            ) var createdAt                          : String? = null,
    @SerializedName("id"                                    ) var id                                 : Int?    = null

)
// Get Crop Stage
 data class GetCropStage(
    @SerializedName("data" ) var data : ArrayList<Data> = arrayListOf()
 )
data class Data (

    @SerializedName("id"                                    ) var id                                 : Int?       = null,
    @SerializedName("fruit_pruning_fruit_pruning"           ) var fruitPruningFruitPruning           : String?    = null,
    @SerializedName("fruit_pruning_bud_break"               ) var fruitPruningBudBreak               : String?    = null,
    @SerializedName("fruit_pruning_removal_of_excessive"    ) var fruitPruningRemovalOfExcessive     : String?    = null,
    @SerializedName("fruit_pruning_shoot_development"       ) var fruitPruningShootDevelopment       : String?    = null,
    @SerializedName("fruit_pruning_flowering"               ) var fruitPruningFlowering              : String?    = null,
    @SerializedName("fruit_pruning_fruit_set"               ) var fruitPruningFruitSet               : String?    = null,
    @SerializedName("fruit_pruning_berry_development"       ) var fruitPruningBerryDevelopment       : String?    = null,
    @SerializedName("fruit_pruning_beginning_of_veraison"   ) var fruitPruningBeginningOfVeraison    : String?    = null,
    @SerializedName("fruit_pruning_harvest"                 ) var fruitPruningHarvest                : String?    = null,
    @SerializedName("fruit_pruning_rest_period"             ) var fruitPruningRestPeriod             : String?    = null,
    @SerializedName("foundation_pruning_foundation_pruning" ) var foundationPruningFoundationPruning : String?    = null,
    @SerializedName("foundation_pruning_bud_break"          ) var foundationPruningBudBreak          : String?    = null,
    @SerializedName("foundation_pruning_cane_thinning"      ) var foundationPruningCaneThinning      : String?    = null,
    @SerializedName("foundation_pruning_sub_cane"           ) var foundationPruningSubCane           : String?    = null,
    @SerializedName("foundation_pruning_topping"            ) var foundationPruningTopping           : String?    = null,
    @SerializedName("plot_id"                               ) var plotId                             : Int?       = null,
    @SerializedName("account_no_id"                         ) var accountNoId                        : Int?       = null,
    @SerializedName("farm_id"                               ) var farmId                             : Int?       = null,
    @SerializedName("created_at"                            ) var createdAt                          : String?    = null,
    @SerializedName("account_no"                            ) var accountNo                          : AccountNo? = AccountNo(),
    @SerializedName("farm"                                  ) var farm                               : Farm?      = Farm(),
    @SerializedName("plot"                                  ) var plot                               : Plot?      = Plot()
)
data class Plot (

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
    @SerializedName("crop_year"             ) var cropYear            : String? = null,
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
    @SerializedName("soil_type_id"          ) var soilTypeId          : String? = null,
    @SerializedName("crop_variety_id"       ) var cropVarietyId       : String? = null

)
data class Farm (

    @SerializedName("id"                  ) var id               : Int?    = null,
    @SerializedName("farm_name"           ) var farmName         : String? = null,
    @SerializedName("farm_center"         ) var farmCenter       : ArrayList<FarmCenter> = arrayListOf(),
    @SerializedName("farm_area"           ) var farmArea         : String?               = null,
    @SerializedName("farm_json"           ) var farmJson         : ArrayList<FarmJson>   = arrayListOf(),
    @SerializedName("farm_water_source"   ) var farmWaterSource  : String? = null,
    @SerializedName("farm_pump_hp"        ) var farmPumpHp       : String? = null,
    @SerializedName("farm_pump_type"      ) var farmPumpType     : String? = null,
    @SerializedName("farm_pump_depth"     ) var farmPumpDepth    : String? = null,
    @SerializedName("farm_pump_pipe_size" ) var farmPumpPipeSize : String? = null,
    @SerializedName("farm_pump_flow_rate" ) var farmPumpFlowRate : String? = null,
    @SerializedName("is_primary"          ) var isPrimary        : Int?    = null,
    @SerializedName("updated_at"          ) var updatedAt        : String? = null,
    @SerializedName("account_no_id"       ) var accountNoId      : Int?    = null

)
data class AccountNo (

    @SerializedName("id"              ) var id             : Int?    = null,
    @SerializedName("account_no"      ) var accountNo      : String? = null,
    @SerializedName("account_type"    ) var accountType    : String? = null,
    @SerializedName("is_active"       ) var isActive       : Int?    = null,
    @SerializedName("default_modules" ) var defaultModules : Int?    = null,
    @SerializedName("subscription"    ) var subscription   : Int?    = null

)
data class FarmCenter (

    @SerializedName("latitude"  ) var latitude  : Double? = null,
    @SerializedName("longitude" ) var longitude : Double? = null

)
data class FarmJson (

    @SerializedName("latitude"  ) var latitude  : Double? = null,
    @SerializedName("longitude" ) var longitude : Double? = null

)