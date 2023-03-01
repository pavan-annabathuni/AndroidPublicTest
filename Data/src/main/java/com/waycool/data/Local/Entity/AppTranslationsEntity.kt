package com.waycool.data.Local.Entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "app_translations")@Keep
data class AppTranslationsEntity(
    @PrimaryKey var id          : Int?    = null,
    @ColumnInfo var appKey      : String? = null,
    @ColumnInfo var appValue    : String? = null,
    @ColumnInfo var createdAt   : String? = null,
    @ColumnInfo var appValueTag : String? = null

)
