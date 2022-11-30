package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class MyFarmsDTO(
    @SerializedName("status"  ) var status  : Boolean?        = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("data"    ) var data    : ArrayList<MyFarmsData> = arrayListOf()
)

data class MyFarmsData (

    @SerializedName("id"                  ) var id               : Int?    = null,
    @SerializedName("farm_name"           ) var farmName         : String? = null,
    @SerializedName("farm_center"         ) var farmCenter       : String? = null,
    @SerializedName("farm_area"           ) var farmArea         : String? = null,
    @SerializedName("farm_json"           ) var farmJson         : String? = null,
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