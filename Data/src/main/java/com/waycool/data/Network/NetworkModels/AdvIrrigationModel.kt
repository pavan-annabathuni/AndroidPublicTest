package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class AdvIrrigationModel(
    @SerializedName("status"  ) var status  : Boolean? = null,
    @SerializedName("message" ) var message : String?  = null,
    @SerializedName("data"    ) var data    : AdvIrrigationData?    = AdvIrrigationData()

)
data class AdvIrrigationData (

    @SerializedName("irrigation" ) var irrigation : Irrigation?       = Irrigation(),
    @SerializedName("disease"    ) var disease    : ArrayList<Disease> = arrayListOf(),
    @SerializedName("gdd"        ) var gdd        : Gdd?              = Gdd()

)

data class Irrigation (

    @SerializedName("current_data"        ) var currentData        : CurrentData?                  = CurrentData(),
    @SerializedName("historic_data"       ) var historicData       : ArrayList<HistoricData>       = arrayListOf(),
    @SerializedName("irrigation_forecast" ) var irrigationForecast : IrrigationForecast?     = IrrigationForecast()

)
data class IrrigationForecast (

    @SerializedName("mad"         ) var mad        : ArrayList<Int>    = arrayListOf(),
    @SerializedName("etc"         ) var etc        : ArrayList<String> = arrayListOf(),
    @SerializedName("days"        ) var days       : ArrayList<String> = arrayListOf(),
    @SerializedName("rain_precip" ) var rainPrecip : ArrayList<String> = arrayListOf(),
    @SerializedName("depletion"   ) var depletion  : ArrayList<String> = arrayListOf(),
    @SerializedName("eto"         ) var eto        : ArrayList<Double> = arrayListOf()

)
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

)
data class Disease (

    @SerializedName("id"               ) var id              : Int?    = null,
    @SerializedName("probability"      ) var probability     : Double? = null,
    @SerializedName("input_parameters" ) var inputParameters : String? = null,
    @SerializedName("translation"      ) var translation     : String? = null,
    @SerializedName("created_at"       ) var createdAt       : String? = null,
    @SerializedName("updated_at"       ) var updatedAt       : String? = null,
    @SerializedName("deleted_at"       ) var deletedAt       : String? = null,
    @SerializedName("crop_id"          ) var cropId          : Int?    = null,
    @SerializedName("disease_id"       ) var diseaseId       : Int?    = null,
    @SerializedName("account_no_id"    ) var accountNoId     : Int?    = null,
    @SerializedName("plot_id"          ) var plotId          : Int?    = null,
    @SerializedName("disease"          ) var disease         : DiseaseType = DiseaseType()

)
data class DiseaseType (

    @SerializedName("id"           ) var id          : Int?    = null,
    @SerializedName("disease_name" ) var diseaseName : String? = null,
    @SerializedName("disease_type" ) var diseaseType : String? = null,
    @SerializedName("disease_img"  ) var diseaseImg  : String? = null

)
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
    @SerializedName("irrigation"        ) var irrigation       : Int?    = null,
    @SerializedName("depletion_current" ) var depletionCurrent : Int?    = null,
    @SerializedName("eto_current"       ) var etoCurrent       : Int?    = null,
    @SerializedName("crop_factor"       ) var cropFactor       : Int?    = null,
    @SerializedName("etc"               ) var etc              : Int?    = null,
    @SerializedName("mad"               ) var mad              : Int?    = null,
    @SerializedName("vol_per_plant"     ) var volPerPlant      : Int?    = null,
    @SerializedName("vol_per_farm"      ) var volPerFarm       : Int?    = null,
    @SerializedName("created_at"        ) var createdAt        : String? = null,
    @SerializedName("updated_at"        ) var updatedAt        : String? = null

)