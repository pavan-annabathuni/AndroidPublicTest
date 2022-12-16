package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class NdviModel(
    @SerializedName("status"  ) var status  : Boolean?        = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("data"    ) var data    : ArrayList<NdviData> = arrayListOf()
)
data class NdviData(
    @SerializedName("id"             ) var id            : Int?    = null,
    @SerializedName("polygon_name"   ) var polygonName   : String? = null,
    @SerializedName("polygon_id"     ) var polygonId     : String? = null,
    @SerializedName("no_of_images"   ) var noOfImages    : Int?    = null,
    @SerializedName("tile_date"      ) var tileDate      : String? = null,
    @SerializedName("cloud_coverage" ) var cloudCoverage : Double? = null,
    @SerializedName("satellite_type" ) var satelliteType : String? = null,
    @SerializedName("truecolor_tile" ) var truecolorTile : String? = null,
    @SerializedName("ndvi_tile"      ) var ndviTile      : String? = null,
    @SerializedName("min_ndvi"       ) var minNdvi       : Double? = null,
    @SerializedName("max_ndvi"       ) var maxNdvi       : Double? = null,
    @SerializedName("mean_ndvi"      ) var meanNdvi      : Double? = null,
    @SerializedName("created_at"     ) var createdAt     : String? = null,
    @SerializedName("account_no_id"  ) var accountNoId   : Int?    = null,
    @SerializedName("farm_id"        ) var farmId        : Int?    = null
)
