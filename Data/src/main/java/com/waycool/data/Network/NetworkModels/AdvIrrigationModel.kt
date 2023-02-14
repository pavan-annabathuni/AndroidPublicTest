package com.waycool.data.Network.NetworkModels

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

data class AdvIrrigationModel(
    @SerializedName("status"  ) var status  : Boolean? = null,
    @SerializedName("message" ) var message : String?  = null,
    @SerializedName("data"    ) var data    : AdvIrrigationData?    = AdvIrrigationData()

)
data class AdvIrrigationData (

    @SerializedName("irrigation" ) var irrigation : Irrigation?       = Irrigation(),
    @SerializedName("gdd"        ) var gdd        : Gdd?              = Gdd()

)

@Parcelize
data class Irrigation (
    @SerializedName("current_data"        ) var currentData        : @RawValue CurrentData?                  = CurrentData(),
    @SerializedName("historic_data"       ) var historicData       : @RawValue List<HistoricData>            = emptyList(),
    @SerializedName("irrigation_forecast" ) var irrigationForecast : @RawValue IrrigationForecast?           = IrrigationForecast()

):Parcelable
data class IrrigationForecast (

    @SerializedName("mad"         ) var mad        : List<Double>    = emptyList(),
    @SerializedName("etc"         ) var etc        : List<String> = emptyList(),
    @SerializedName("days"        ) var days       : List<String> = emptyList(),
    @SerializedName("rain_precip" ) var rainPrecip : List<String> = emptyList(),
    @SerializedName("depletion"   ) var depletion  : List<String> = emptyList(),
    @SerializedName("eto"         ) var eto        : List<Double> = emptyList()

)
@Parcelize
data class HistoricData (

    @SerializedName("id"                ) var id               : Int?    = null,
    @SerializedName("irrigation"        ) var irrigation       : String? = null,
    @SerializedName("depletion_current" ) var depletionCurrent : String? = null,
    @SerializedName("eto_current"       ) var etoCurrent       : String? = null,
    @SerializedName("crop_factor"       ) var cropFactor       : String? = null,
    @SerializedName("etc"               ) var etc              : String? = null,
    @SerializedName("mad"               ) var mad              : String? = null,
    @SerializedName("vol_per_plant"     ) var volPerPlant      : String? = null,
    @SerializedName("vol_per_farm"      ) var volPerFarm       : String? = null,
    @SerializedName("created_at"        ) var createdAt        : String? = null,
    @SerializedName("updated_at"        ) var updatedAt        : String? = null,
    @SerializedName("deleted_at"        ) var deletedAt        : String? = null

):Parcelable

data class Gdd (

    @SerializedName("id"           ) var id          : Int?    = null,
    @SerializedName("calling_date" ) var callingDate : String? = null,
    @SerializedName("crop_stage"   ) var cropStage   : String? = null,
    @SerializedName("crop_days"    ) var cropDays    : Double? = null,
    @SerializedName("gdd_prev"     ) var gddPrev     : Double? = null,
    @SerializedName("gdd_today"    ) var gddToday    : Double? = null,
    @SerializedName("gdd_max"      ) var gddMax      : Double? = null,
    @SerializedName("created_at"   ) var createdAt   : String? = null,
    @SerializedName("updated_at"   ) var updatedAt   : String? = null,
    @SerializedName("deleted_at"   ) var deletedAt   : String? = null,
    @SerializedName("plot_id"      ) var plotId      : Int?    = null,
    @SerializedName("crop_id"      ) var cropId      : Int?    = null

)
data class CurrentData (

    @SerializedName("id"                ) var id               : Int?    = null,
    @SerializedName("irrigation"        ) var irrigation       : Double?    = null,
    @SerializedName("depletion_current" ) var depletionCurrent : Double?    = null,
    @SerializedName("eto_current"       ) var etoCurrent       : Double?    = null,
    @SerializedName("crop_factor"       ) var cropFactor       : Double?    = null,
    @SerializedName("etc"               ) var etc              : Double?    = null,
    @SerializedName("mad"               ) var mad              : Double?    = null,
    @SerializedName("vol_per_plant"     ) var volPerPlant      : Double?    = null,
    @SerializedName("vol_per_farm"      ) var volPerFarm       : Double?    = null,
    @SerializedName("created_at"        ) var createdAt        : String? = null,
    @SerializedName("updated_at"        ) var updatedAt        : String? = null

)
