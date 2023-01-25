package com.waycool.data.repository.DomainMapper

import com.waycool.data.Local.Entity.VansCategoryEntity
import com.waycool.data.Network.NetworkModels.TrackerData
import com.waycool.data.Network.NetworkModels.VarietyCrop
import com.waycool.data.repository.domainModels.TrackerDemain
import com.waycool.data.repository.domainModels.VansCategoryDomain
import com.waycool.data.repository.domainModels.VarietyCropDomain
import com.waycool.data.repository.util.DomainMapper

class VarietyCropDomainMapper: DomainMapper<VarietyCropDomain, VarietyCrop>{
    override fun mapToDomain(dto: VarietyCrop): VarietyCropDomain {
        return VarietyCropDomain(
            id = dto.id,
            varietyTag = dto.varietyTag,
            variety = dto.variety,
            cropId = dto.cropId,
            purpose = dto.purpose
        )
    }
    fun toDomainList(initial: List<VarietyCrop>): List<VarietyCropDomain> {
        return initial.map {
            mapToDomain(it)
        }
    }

}