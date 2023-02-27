package com.waycool.data.Network.NetworkModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class GeocodeDTO(

    @SerializedName("results" ) var results : ArrayList<ResultsDTO> = arrayListOf(),
    @SerializedName("status"  ) var status  : String?            = null

)

@Keep
data class ResultsDTO (

    @SerializedName("address_components" ) var addressComponents : ArrayList<AddressComponentsDTO> = arrayListOf(),
    @SerializedName("formatted_address"  ) var formattedAddress  : String?                      = null

)

@Keep
data class AddressComponentsDTO (

    @SerializedName("long_name"  ) var longName  : String?           = null,
    @SerializedName("short_name" ) var shortName : String?           = null,
    @SerializedName("types"      ) var types     : ArrayList<String> = arrayListOf()

)