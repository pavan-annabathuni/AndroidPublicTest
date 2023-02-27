package com.waycool.data.Local.Entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "pest_disease") @Keep
data class PestDiseaseEntity(
    @ColumnInfo(name = "crop_id") var cropId: Int? = null,
    @ColumnInfo(name = "disease_name") var diseaseName: String? = null,
    @PrimaryKey @ColumnInfo(name = "disease_id") var diseaseId: Int? = null,
    @ColumnInfo(name = "thumb") var thumb: String? = null,
    @ColumnInfo(name = "image_url") var imageUrl: String? = null,
    @ColumnInfo(name = "audio_url") var audioUrl: String? = null,
    @ColumnInfo(name = "mode_of_infestation") var modeOfInfestation: String? = null,
    @ColumnInfo(name = "symptoms") var symptoms: String? = null,
    @ColumnInfo(name = "cultural") var cultural: String? = null,
    @ColumnInfo(name = "biological") var biological: String? = null,
    @ColumnInfo(name = "chemical") var chemical: String? = null,
)
