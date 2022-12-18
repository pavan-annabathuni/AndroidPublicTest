package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class CropStageModel(
    @SerializedName("status"  ) var status  : Boolean?        = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("data"    ) var data    : ArrayList<CropStageData> = arrayListOf()
)
data class CropStageData(
    @SerializedName("id"              ) var id            : Int?    = null,
    @SerializedName("crop_id"         ) var cropId        : Int?    = null,
    @SerializedName("stage_name"      ) var stageName     : String? = null,
    @SerializedName("stage_icon"      ) var stageIcon     : String? = null,
    @SerializedName("crop_variety_id" ) var cropVarietyId : String? = null,
    @SerializedName("created_at"      ) var createdAt     : String? = null,
    @SerializedName("date"            ) var date              : String? = null,
    @SerializedName("plot_id"         ) var plotId        : Int?    = null,
    @SerializedName("account_no_id"   ) var accountNoId   : Int?    = null,
    @SerializedName("crop"            ) var crop          : Crop?   = Crop()
)
data class Crop (

    @SerializedName("id"        ) var id       : Int?    = null,
    @SerializedName("crop_name" ) var cropName : String? = null,
    @SerializedName("crop_logo" ) var cropLogo : String? = null

)

data class UpdateCropStage(
    @SerializedName("status"  ) var status  : Boolean? = null,
    @SerializedName("message" ) var message : String?  = null,
    @SerializedName("data"    ) var data    : CropStageData?    = CropStageData()
)

data class UpdateStageData (

    @SerializedName("account_no_id"        ) var accountNoId       : String? = null,
    @SerializedName("plot_id"              ) var plotId            : String? = null,
    @SerializedName("crop_stage_master_id" ) var cropStageMasterId : String? = null,
    @SerializedName("date"                 ) var date              : String? = null,
    @SerializedName("created_at"           ) var createdAt         : String? = null,
    @SerializedName("id"                   ) var id                : Int?    = null

)