package com.waycool.data.Network.NetworkModels

import com.google.gson.annotations.SerializedName

data class GeocodeDTO(

    @SerializedName("results" ) var results : ArrayList<ResultsDTO> = arrayListOf(),
    @SerializedName("status"  ) var status  : String?            = null

)

data class ResultsDTO (

    @SerializedName("address_components" ) var addressComponents : ArrayList<AddressComponentsDTO> = arrayListOf(),
    @SerializedName("formatted_address"  ) var formattedAddress  : String?                      = null

)

data class AddressComponentsDTO (

    @SerializedName("long_name"  ) var longName  : String?           = null,
    @SerializedName("short_name" ) var shortName : String?           = null,
    @SerializedName("types"      ) var types     : ArrayList<String> = arrayListOf()

)