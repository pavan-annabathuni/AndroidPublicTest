package com.waycool.data.repository.DomainMapper

import com.waycool.data.Network.NetworkModels.CheckSoilTestData
import com.waycool.data.repository.domainModels.CheckSoilTestDomain
import com.waycool.data.repository.util.DomainMapper

class CheckSoilTestLabMapper: DomainMapper<CheckSoilTestDomain, CheckSoilTestData> {
    override fun mapToDomain(dto: CheckSoilTestData): CheckSoilTestDomain {
        return CheckSoilTestDomain(
            onpAddress = dto.onpAddress,
            onpCountry = dto.onpCountry,
            onpDistanceKm = dto.onpDistanceKm,
            onpDistanceMeters = dto.onpDistanceMeters,
            onpDistrict = dto.onpDistrict,
            onpDuration = dto.onpDuration,
            onpId = dto.onpId,
            onpLat = dto.onpLat,
            onpLatLongRange = dto.onpLatLongRange,
            onpLong = dto.onpLong,
            onpName = dto.onpName,

            onpPincode = dto.onpPincode,
//            onp_service_pincodes = dto.onp_service_pincodes,
            onpState = dto.onpState,
            onpVillage = dto.onpVillage

        )
    }
    fun toDomainList(initial: List<CheckSoilTestData>): List<CheckSoilTestDomain> {
        return initial.map {
            mapToDomain(it)
        }
    }
}