package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

class MandiModel(
    @SerializedName("status"  ) var status  : Boolean? = null,
    @SerializedName("message" ) var message : String?  = null,
    @SerializedName("data"    ) var data    : MandiData?    = MandiData()
)

data class MandiData(
@SerializedName("page"                   ) var page                   : Int?               = null,
@SerializedName("numberOfRecordsPerPage" ) var numberOfRecordsPerPage : Int?               = null,
@SerializedName("startFrom"              ) var startFrom              : Int?               = null,
@SerializedName("total_pages"            ) var totalPages             : Int?               = null,
@SerializedName("total_results"          ) var totalResults           : Int?               = null,
@SerializedName("records"                ) var records                : List<MandiRecord> = emptyList()

)

data class MandiRecord(
    @SerializedName("crop_te"         ) var cropTe        : String? = null,
    @SerializedName("market_te"       ) var marketTe      : String? = null,
    @SerializedName("crop_master_id"  ) var cropMasterId  : Int?    = null,
    @SerializedName("crop_logo"       ) var cropLogo      : String? = null,
    @SerializedName("market_ta"       ) var marketTa      : String? = null,
    @SerializedName("avg_price"       ) var avgPrice      : Double?    = null,
    @SerializedName("source"          ) var source        : String? = null,
    @SerializedName("market"          ) var market        : String? = null,
    @SerializedName("market_kn"       ) var marketKn      : String? = null,
    @SerializedName("crop_hi"         ) var cropHi        : String? = null,
    @SerializedName("price_status"    ) var priceStatus   : Int?    = null,
    @SerializedName("crop_kn"         ) var cropKn        : String? = null,
    @SerializedName("market_hi"       ) var marketHi      : String? = null,
    @SerializedName("crop_mr"         ) var cropMr        : String? = null,
    @SerializedName("mandi_master_id" ) var mandiMasterId : Int?    = null,
    @SerializedName("sub_record_id"   ) var subRecordId   : String? = null,
    @SerializedName("crop_ta"         ) var cropTa        : String? = null,
    @SerializedName("crop"            ) var crop          : String? = null,
    @SerializedName("market_mr"       ) var marketMr      : String? = null,
    @SerializedName("distance"       ) var distance      : Double? = null,
    @SerializedName("id"              ) var id            : String? = null
)