package com.waycool.data.Network.NetworkModels

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
@Keep
data class CheckSoilTestLabDTO (
//        @SerializedName("status"  ) var status  : Boolean?        = null,
//        @SerializedName("message" ) var message : String?         = null,
//        @SerializedName("data"    ) var data: List<CheckSoilTestData> = emptyList()
        @SerializedName("status"  ) var status  : Boolean?        = null,
        @SerializedName("message" ) var message : String?         = null,
        @SerializedName("data"    ) var data    : List<CheckSoilTestData> = arrayListOf()
        )
@Keep
data class CheckSoilTestData(
//        val onp_address: @RawValue Any? = null,
//        val onp_country: @RawValue Any? = null,
//        val onp_distance_km: @RawValue Any? = null,
//        val onp_distance_meters: @RawValue Any? = null,
//        val onp_district: @RawValue Any? = null,
//        val onp_duration: @RawValue Any? = null,
//        val onp_id: @RawValue Int? = null,
//        val onp_lat: @RawValue Double? = null,
//        val onp_lat_long_range: @RawValue Any? = null,
//        val onp_long: @RawValue Double? = null,
//        val onp_name: @RawValue Any? = null,
//        val onp_pincode: @RawValue Any? = null,
//        val onp_service_pincodes: @RawValue Any? = null,
//        val onp_state: @RawValue Any? = null,
//        val onp_village: @RawValue Any? = null
        @SerializedName("onp_id"              ) var onpId             : Int?    = null,
        @SerializedName("onp_name"            ) var onpName           : String? = null,
        @SerializedName("onp_lat"             ) var onpLat            : String? = null,
        @SerializedName("onp_long"            ) var onpLong           : String? = null,
        @SerializedName("onp_village"         ) var onpVillage        : String? = null,
        @SerializedName("onp_address"         ) var onpAddress        : String? = null,
        @SerializedName("onp_pincode"         ) var onpPincode        : String? = null,
        @SerializedName("onp_district"        ) var onpDistrict       : String? = null,
        @SerializedName("onp_state"           ) var onpState          : String? = null,
        @SerializedName("onp_country"         ) var onpCountry        : String? = null,
        @SerializedName("onp_lat_long_range"  ) var onpLatLongRange   : String? = null,
        @SerializedName("onp_distance_km"     ) var onpDistanceKm     : String? = null,
        @SerializedName("onp_duration"        ) var onpDuration       : String? = null,
        @SerializedName("onp_distance_meters" ) var onpDistanceMeters : Int?    = null

)