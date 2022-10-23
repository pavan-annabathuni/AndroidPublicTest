package com.waycool.data.repository.DomainMapper

import com.waycool.data.Network.NetworkModels.TrackerData
import com.waycool.data.repository.domainModels.TrackerDemain
import com.waycool.data.repository.util.DomainMapper


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