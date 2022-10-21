package com.waycool.data.Local.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "crop_information")
data class CropInformationEntityData(
        @PrimaryKey
        val id: Int?,
        @ColumnInfo(name = "crop_id")val crop_id: Int? = null,
        @ColumnInfo(name = "label_name")val label_name: String? =null,
        @ColumnInfo(name = "label_value")val label_value: String? = null

)