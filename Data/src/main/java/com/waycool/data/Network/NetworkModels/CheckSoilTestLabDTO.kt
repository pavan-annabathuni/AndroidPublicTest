package com.waycool.data.Network.NetworkModels

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

data class CheckSoilTestLabDTO (
        @SerializedName("status"  ) var status  : Boolean?        = null,
        @SerializedName("message" ) var message : String?         = null,
        @SerializedName("data"    ) var data: List<CheckSoilTestData> = emptyList()
        )
@Parcelize
data class CheckSoilTestData(
        val onp_address: @RawValue Any? = null,
        val onp_country: @RawValue Any? = null,
        val onp_distance_km: @RawValue Any? = null,
        val onp_distance_meters: @RawValue Any? = null,
        val onp_district: @RawValue Any? = null,
        val onp_duration: @RawValue Any? = null,
        val onp_id: @RawValue Int? = null,
        val onp_lat: @RawValue Double? = null,
        val onp_lat_long_range: @RawValue Any? = null,
        val onp_long: @RawValue Double? = null,
        val onp_name: @RawValue Any? = null,
        val onp_pincode: @RawValue Any? = null,
        val onp_service_pincodes: @RawValue Any? = null,
        val onp_state: @RawValue Any? = null,
        val onp_village: @RawValue Any? = null
): Parcelable