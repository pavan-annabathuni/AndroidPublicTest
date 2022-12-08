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
    @SerializedName("evi_tile"       ) var eviTile       : String? = null,
    @SerializedName("evi2_tile"      ) var evi2Tile      : String? = null,
    @SerializedName("nri_tile"       ) var nriTile       : String? = null,
    @SerializedName("dswi_tile"      ) var dswiTile      : String? = null,
    @SerializedName("ndwi_tile"      ) var ndwiTile      : String? = null,
    @SerializedName("stats"          ) var stats         : String? = null,
    @SerializedName("data"           ) var data2          : String? = null,
    @SerializedName("min_ndvi"       ) var minNdvi       : Double? = null,
    @SerializedName("max_ndvi"       ) var maxNdvi       : Double? = null,
    @SerializedName("mean_ndvi"      ) var meanNdvi      : Double? = null,
    @SerializedName("created_at"     ) var createdAt     : String? = null,
    @SerializedName("updated_at"     ) var updatedAt     : String? = null,
    @SerializedName("deleted_at"     ) var deletedAt     : String? = null,
    @SerializedName("account_no_id"  ) var accountNoId   : Int?    = null,
    @SerializedName("farm_id"        ) var farmId        : Int?    = null
)
