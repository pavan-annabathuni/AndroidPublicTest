package com.waycool.data.Local.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MandiKeysEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,

    val prevPage: Int?,
    val nextPage: Int?
)
