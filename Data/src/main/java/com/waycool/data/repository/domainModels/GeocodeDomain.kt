package com.waycool.data.repository.domainModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep

data class GeocodeDomain(

    var results: ArrayList<ResultsDomain> = arrayListOf(),
    var status: String? = null

)
@Keep
data class ResultsDomain(

//    var addressComponents: ArrayList<AddressComponentsDomain> = arrayListOf(),
    var formattedAddress: String? = null,
    var country: String? = null,
    var state: String? = null,
    var district: String? = null,
    var locality: String? = null,
    var subLocality: String? = null,
    var pincode: String? = null
)

//data class AddressComponentsDomain(
//
//    var longName: String? = null,
//    var shortName: String? = null,
//    var types: ArrayList<String> = arrayListOf()
//
//)