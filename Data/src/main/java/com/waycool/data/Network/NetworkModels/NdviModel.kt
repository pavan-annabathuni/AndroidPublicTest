package com.waycool.data.Network.NetworkModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class NdviModel(
    @SerializedName("status"  ) var status  : Boolean?        = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("data"    ) var data    : ArrayList<NdviData> = arrayListOf()
)

@Keep
data class NdviData(
    @SerializedName("dt"    ) var dt    : Long?    = null,
    @SerializedName("type"  ) var type  : String? = null,
    @SerializedName("dc"    ) var dc    : Double?    = null,
    @SerializedName("cl"    ) var cl    : Double? = null,
    @SerializedName("tile"  ) var tile  : Tile?   = Tile(),
    @SerializedName("stats" ) var stats : Stats?  = Stats()
)
@Keep
data class Stats (

    @SerializedName("ndvi" ) var ndvi : String? = null,
    @SerializedName("evi"  ) var evi  : String? = null,
    @SerializedName("evi2" ) var evi2 : String? = null,
    @SerializedName("nri"  ) var nri  : String? = null,
    @SerializedName("dswi" ) var dswi : String? = null,
    @SerializedName("ndwi" ) var ndwi : String? = null

)
@Keep
data class Tile (

    @SerializedName("truecolor"  ) var truecolor  : String? = null,
    @SerializedName("falsecolor" ) var falsecolor : String? = null,
    @SerializedName("ndvi"       ) var ndvi       : String? = null,
    @SerializedName("evi"        ) var evi        : String? = null,
    @SerializedName("evi2"       ) var evi2       : String? = null,
    @SerializedName("nri"        ) var nri        : String? = null,
    @SerializedName("dswi"       ) var dswi       : String? = null,
    @SerializedName("ndwi"       ) var ndwi       : String? = null

)
@Keep
data class NDVIMean(
    @SerializedName("mean"  ) var mean  : Double? = null,
    )
