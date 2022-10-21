package com.waycool.data.Local.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

//@Entity(tableName = "add_crop_type")
data class AddCropTypeEntity(
    val id: Int? = null,
    val isSelected:Boolean?=null,
    val soil_type: String? = null
//    @PrimaryKey var addCropType: Int,
//    @SerializedName("id") var id  : Int?    = null,
//    @SerializedName("isSelected") var isSelected:Boolean?=null,
//    @SerializedName("soil_type") var soilType : String? = null
)
