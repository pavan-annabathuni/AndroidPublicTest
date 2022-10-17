package com.waycool.data.Local.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "tags")
data class TagsEntity(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "tag_name")
    val tag_name: String?,
    @ColumnInfo(name = "translation")
    val translation: String?
)