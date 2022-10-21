package com.waycool.data.Repository.DomainMapper

import com.waycool.data.Network.NetworkModels.CheckSoilTestData
import com.waycool.data.Network.NetworkModels.TrackerData
import com.waycool.data.Repository.DomainModels.CheckSoilTestDomain
import com.waycool.data.Repository.DomainModels.TrackerDemain
import com.waycool.data.Repository.util.DomainMapper

class TrackerDomainMapper: DomainMapper<TrackerDemain, TrackerData> {
    override fun mapToDomain(dto: TrackerData): TrackerDemain {
        return TrackerDemain(
            is_approved = dto.is_approved,
            date = dto.date,
            id = dto.id,
            title = dto.title
        )

    }
    fun toDomainList(initial: List<TrackerData>): List<TrackerDemain> {
        return initial.map {
            mapToDomain(it)
        }
    }
}