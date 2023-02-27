package com.waycool.data.Network.NetworkModels

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
@Keep
data class AdvIrrigationModel(
    @SerializedName("status"  ) var status  : Boolean? = null,
    @SerializedName("message" ) var message : String?  = null,
    @SerializedName("data"    ) var data    : AdvIrrigationData?    = AdvIrrigationData()

)

@Keep
data class AdvIrrigationData (

    @SerializedName("irrigation" ) var irrigation : Irrigation?       = Irrigation(),
    @SerializedName("gdd"        ) var gdd        : Gdd?              = Gdd()

)

@Parcelize @Keep
data class Irrigation (
    @SerializedName("current_data"        ) var currentData        : CurrentData?        = CurrentData(),
    @SerializedName("historic_data"       ) var historicData       : ArrayList<HistoricData?>?   = arrayListOf(),
    @SerializedName("irrigation_forecast" ) var irrigationForecast : IrrigationForecast? = IrrigationForecast()
):Parcelable

@Parcelize @Keep
data class IrrigationForecast (

    @SerializedName("mad"         ) var mad        : ArrayList<Double?> = arrayListOf(),
    @SerializedName("etc"         ) var etc        : ArrayList<String?> = arrayListOf(),
    @SerializedName("days"        ) var days       : ArrayList<String?> = arrayListOf(),
    @SerializedName("rain_precip" ) var rainPrecip : ArrayList<String?> = arrayListOf(),
    @SerializedName("depletion"   ) var depletion  : ArrayList<String?> = arrayListOf(),
    @SerializedName("eto"         ) var eto        : ArrayList<Double?> = arrayListOf()

):Parcelable

@Parcelize @Keep
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
    @SerializedName("rainfall"          ) var rainfall         : String? = null,
    @SerializedName("deleted_at"        ) var deletedAt        : String? = null

):Parcelable

@Keep
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

@Parcelize @Keep
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

):Parcelable
