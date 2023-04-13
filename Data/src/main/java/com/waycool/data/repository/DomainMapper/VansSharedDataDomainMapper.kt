package com.waycool.data.repository.DomainMapper

import com.waycool.data.Network.NetworkModels.VansSharedData
import com.waycool.data.repository.domainModels.VansSharedDataDomain
import com.waycool.data.repository.util.DomainMapper

class VansSharedDataDomainMapper: DomainMapper<VansSharedDataDomain, VansSharedData> {
    override fun mapToDomain(dto: VansSharedData): VansSharedDataDomain {
        return VansSharedDataDomain(
            id = dto.id,
        vans_type = dto.vans_type,
        title = dto.title,
        desc = dto.desc,
        content_url = dto.content_url,
        thumbnail_url = dto.thumbnail_url,
        audio_url = dto.audio_url,
        source_name = dto.source_name,
        category_id = dto.category_id,
        crop_id = dto.crop_id,
        updated_at = dto.updated_at
        )
    }
    fun toDomainList(initial: List<VansSharedData>): List<VansSharedDataDomain> {
        return initial.map {
            mapToDomain(it)
        }
    }

}