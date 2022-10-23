package com.waycool.data.repository.DomainMapper

import com.waycool.data.Network.NetworkModels.CheckSoilTestData
import com.waycool.data.repository.domainModels.CheckSoilTestDomain
import com.waycool.data.repository.util.DomainMapper

class CheckSoilTestLabMapper: DomainMapper<CheckSoilTestDomain, CheckSoilTestData> {
    override fun mapToDomain(dto: CheckSoilTestData): CheckSoilTestDomain {
        return CheckSoilTestDomain(
            onp_address = dto.onp_address,
            onp_country = dto.onp_country,
            onp_distance_km = dto.onp_distance_km,
            onp_distance_meters = dto.onp_distance_meters,
            onp_district = dto.onp_district,
            onp_duration = dto.onp_duration,
            onp_id = dto.onp_id,
            onp_lat = dto.onp_lat,
            onp_lat_long_range = dto.onp_lat_long_range,
            onp_long = dto.onp_long,
            onp_name = dto.onp_name,
            onp_pincode = dto.onp_pincode,
            onp_service_pincodes = dto.onp_service_pincodes,
            onp_state = dto.onp_state

        )
    }
    fun toDomainList(initial: List<CheckSoilTestData>): List<CheckSoilTestDomain> {
        return initial.map {
            mapToDomain(it)
        }
    }
}