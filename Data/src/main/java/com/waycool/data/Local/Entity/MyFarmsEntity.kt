package com.waycool.data.Local.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "my_farms")
@TypeConverters
data class MyFarmsEntity(
    @PrimaryKey
    @ColumnInfo(name="id"                  ) var id               : Int?    = null,
    @ColumnInfo(name="farm_name"           ) var farmName         : String? = null,
    @ColumnInfo(name="farm_location"       ) var farmLocation         : String? = null,
    @ColumnInfo(name="farm_center"         ) var farmCenter       : ArrayList<LatLng>? = null,
    @ColumnInfo(name="farm_area"           ) var farmArea         : String? = null,
    @ColumnInfo(name="farm_json"           ) var farmJson          :ArrayList<LatLng>? = null,
    @ColumnInfo(name="farm_water_source"   ) var farmWaterSource  : ArrayList<String>? = null,
    @ColumnInfo(name="farm_pump_hp"        ) var farmPumpHp       : String? = null,
    @ColumnInfo(name="farm_pump_type"      ) var farmPumpType     : String? = null,
    @ColumnInfo(name="farm_pump_depth"     ) var farmPumpDepth    : String? = null,
    @ColumnInfo(name="farm_pump_pipe_size" ) var farmPumpPipeSize : String? = null,
    @ColumnInfo(name="farm_pump_flow_rate" ) var farmPumpFlowRate : String? = null,
    @ColumnInfo(name="is_primary"          ) var isPrimary        : Int?    = null,
    @ColumnInfo(name="updated_at"          ) var updatedAt        : String? = null,
    @ColumnInfo(name="account_no_id"       ) var accountNoId      : Int?    = null
)
