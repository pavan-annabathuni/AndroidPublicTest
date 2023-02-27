package com.waycool.data.Local.Entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.waycool.data.Network.NetworkModels.CropDataData
@Entity(tableName = "ai_history") @Keep
data class AiCropHistoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") var id: Int?    = null,
    @ColumnInfo(name = "image_url") var imageUrl: String? = null,
    @ColumnInfo(name = "prediction") var prediction: String? = null,
    @ColumnInfo(name = "probability") var probability: Double? = null,
    @ColumnInfo(name = "user_feedback") var userFeedback: String? = null,
    @ColumnInfo(name = "disease_id") var diseaseId: Int?    = null,
    @ColumnInfo(name = "crop_id") var cropId: Int?    = null,
    @ColumnInfo(name = "updated_at") var updatedAt: String? = null,
    @ColumnInfo(name = "crop_name") var cropName : String? = null
    )
