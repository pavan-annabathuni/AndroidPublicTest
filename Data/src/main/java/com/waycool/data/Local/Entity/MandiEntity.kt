package com.waycool.data.Local.Entity

import com.google.gson.annotations.SerializedName
import com.waycool.data.Network.NetworkModels.MandiData
import com.waycool.data.repository.domainModels.MandiDomainRecord

data class MandiEntity(
    @SerializedName("status"  ) var status  : Boolean? = null,
    @SerializedName("message" ) var message : String?  = null,
    @SerializedName("data"    ) var data    : MandiEntityData?    = MandiEntityData()
)

data class MandiEntityData(
    var numberOfRecordsPerPage : Int?               = null,
    var page                   : Int?               = null,
    val records: List<MandiEntityRecord> = emptyList(),
    var startFrom              : Int?               = null,
    var totalPages             : Int?               = null,
    var totalResults           : Int?               = null,
)

data class MandiEntityRecord(
     var cropTe        : String? = null,
     var marketTe      : String? = null,
     var cropMasterId  : Int?    = null,
     var cropLogo      : String? = null,
     var marketTa      : String? = null,
     var avgPrice      : Double?    = null,
     var source        : String? = null,
     var market        : String? = null,
     var marketKn      : String? = null,
    var cropHi        : String? = null,
     var priceStatus   : Int?    = null,
     var cropKn        : String? = null,
     var marketHi      : String? = null,
    var cropMr        : String? = null,
     var mandiMasterId : Int?    = null,
     var subRecordId   : String? = null,
    var cropTa        : String? = null,
     var crop          : String? = null,
    var marketMr      : String? = null,
    var id            : String? = null,
     var distance     : Double? = null

)


