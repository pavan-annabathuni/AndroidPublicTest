package com.waycool.data.repository.domainModels

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class CheckSoilTestDomain(
    val onp_address: @RawValue Any? = null,
    val onp_country: @RawValue Any? = null,
    val onp_distance_km: @RawValue Any? = null,
    val onp_distance_meters: @RawValue Any? = null,
    val onp_district: @RawValue Any? = null,
    val onp_duration: @RawValue Any? = null,
    val onp_id: @RawValue Any? = null,
    val onp_lat: @RawValue Any? = null,
    val onp_lat_long_range: @RawValue Any? = null,
    val onp_long: @RawValue Any? = null,
    val onp_name: @RawValue Any? = null,
    val onp_pincode: @RawValue Any? = null,
    val onp_service_pincodes: @RawValue Any? = null,
    val onp_state: @RawValue Any? = null,
    val onp_village: @RawValue Any? = null
): Parcelable