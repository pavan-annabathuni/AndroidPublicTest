package com.waycool.data.Local.Entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "soil_test_history") @Keep
data class SoilTestHistoryEntity (
//    @ColumnInfo val id: Int?=null,
//    val plot_no: String?=null,
//    val soil_test_number: String?=null,
//    val status: String?=null,
//    val updated_at: String?=null,
//    val approve_status:String?=null,
    @PrimaryKey
    @ColumnInfo(name = "id") var id: Int? = null,
    @ColumnInfo(name ="plot_no") var plotNo: String? = null,
    @ColumnInfo(name ="soil_test_number") var soilTestNumber: String? = null,
    @ColumnInfo(name ="status") var status: String? = null,
    @ColumnInfo(name ="approve_status") var approveStatus: String? = null,
    @ColumnInfo(name ="date") var updatedAt: String? = null



)