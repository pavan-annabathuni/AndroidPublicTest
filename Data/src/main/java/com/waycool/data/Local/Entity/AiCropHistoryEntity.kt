package com.waycool.data.Local.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.waycool.data.Network.NetworkModels.CropDataData
@Entity(tableName = "ai_history")
data class AiCropHistoryEntity(
//    val id: Int? = null,
//    val crop_id: Int? = null,
//    val disease_id: Int? = null,
//    val image_url: String? = null,
//    val prediction: String? = null,
//    val probability: Int? = null,
//    val user_feedback: Any? = null,
//    val updated_at: String? = null,
//    @SerializedName("crop") var cropdata: CropData?
    @SerializedName("id"            ) var id: Int?    = null,
    @SerializedName("image_url"     ) var imageUrl: String? = null,
    @SerializedName("prediction"    ) var prediction: String? = null,
    @SerializedName("probability"   ) var probability: Double? = null,
    @SerializedName("user_feedback" ) var userFeedback: String? = null,
    @SerializedName("disease_id"    ) var diseaseId: Int?    = null,
    @SerializedName("crop_id"       ) var cropId: Int?    = null,
    @SerializedName("updated_at"    ) var updatedAt: String? = null,
    @SerializedName("crop"          ) var crop: CropData = CropData()
    )
data class  CropData(
    @SerializedName("id"        ) var id       : Int?    = null,
    @SerializedName("crop_name" ) var cropName : String? = null

//    @SerializedName("crop_name") var name:String?=null
)
