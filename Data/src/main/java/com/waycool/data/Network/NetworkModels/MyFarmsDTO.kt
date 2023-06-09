package com.waycool.data.Network.NetworkModels

import androidx.annotation.Keep
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
@Keep
data class MyFarmsDTO(
    @SerializedName("status"  ) var status  : Boolean?        = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("data"    ) var data    : ArrayList<MyFarmsNetwork> = arrayListOf()
)
@Keep
data class MyFarmsNetwork (
    @SerializedName("id"                  ) var id               : Int?    = null,
    @SerializedName("farm_name"           ) var farmName         : String? = null,
    @SerializedName("farm_location"       ) var farmLocation     : String? = null,
    @SerializedName("farm_center"         ) var farmCenter       : ArrayList<LatLng>? = null,
    @SerializedName("farm_area"           ) var farmArea         : String? = null,
    @SerializedName("farm_json"           ) var farmJson         :ArrayList<LatLng>? = null,
    @SerializedName("farm_water_source"   ) var farmWaterSource  : ArrayList<String>? = null,
    @SerializedName("farm_pump_hp"        ) var farmPumpHp       : String? = null,
    @SerializedName("farm_pump_type"      ) var farmPumpType     : String? = null,
    @SerializedName("farm_pump_depth"     ) var farmPumpDepth    : String? = null,
    @SerializedName("farm_pump_pipe_size" ) var farmPumpPipeSize : String? = null,
    @SerializedName("farm_pump_flow_rate" ) var farmPumpFlowRate : String? = null,
    @SerializedName("is_primary"          ) var isPrimary        : Int?    = null,
    @SerializedName("updated_at"          ) var updatedAt        : String? = null,
    @SerializedName("account_no_id"       ) var accountNoId      : Int?    = null

)