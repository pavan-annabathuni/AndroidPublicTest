package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class PestAndDiseaseModel(
    @SerializedName("status"  ) var status  : Boolean? = null,
    @SerializedName("message" ) var message : String?  = null,
    @SerializedName("data"    ) var data    : DiseaseData?    = DiseaseData()
)

data class DiseaseData (

    @SerializedName("current_data"  ) var currentData  : ArrayList<DiseaseCurrentData>  = arrayListOf(),
    @SerializedName("historic_data" ) var historicData : ArrayList<DiseaseHistoricData> = arrayListOf()

)
data class DiseaseCurrentData (
    @SerializedName("probability"      ) var probability     : Int?     = null,
    @SerializedName("crop_id"          ) var cropId          : Int?     = null,
    @SerializedName("disease_id"       ) var diseaseId       : Int?     = null,
    @SerializedName("account_no_id"    ) var accountNoId     : Int?     = null,
    @SerializedName("plot_id"          ) var plotId          : Int?     = null,
    @SerializedName("created_at"       ) var createdAt       : String?  = null,
    @SerializedName("updated_at"       ) var updatedAt       : String?  = null,
    @SerializedName("probability_desc" ) var probabilityDesc : String?  = null,
    @SerializedName("disease"          ) var disease         : DiseaseDetails? = DiseaseDetails()

)
data class DiseaseDetails (

    @SerializedName("id"           ) var id          : Int?    = null,
    @SerializedName("disease_name" ) var diseaseName : String? = null,
    @SerializedName("disease_name_translated" ) var diseaseNameTranslated : String? = null,
    @SerializedName("disease_type" ) var diseaseType : String? = null,
    @SerializedName("disease_img"  ) var diseaseImg  : String? = null

)
data class DiseaseHistoricData (

    @SerializedName("probability"      ) var probability     : Int?     = null,
    @SerializedName("crop_id"          ) var cropId          : Int?     = null,
    @SerializedName("disease_id"       ) var diseaseId       : Int?     = null,
    @SerializedName("account_no_id"    ) var accountNoId     : Int?     = null,
    @SerializedName("plot_id"          ) var plotId          : Int?     = null,
    @SerializedName("created_at"       ) var createdAt       : String?  = null,
    @SerializedName("updated_at"       ) var updatedAt       : String?  = null,
    @SerializedName("probability_desc" ) var probabilityDesc : String?  = null,
    @SerializedName("disease"          ) var disease         : DiseaseDetails? = DiseaseDetails()


)