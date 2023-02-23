package com.waycool.data.repository.DomainMapper

import com.waycool.data.Local.Entity.AddCropTypeEntity
import com.waycool.data.repository.domainModels.SoilTypeDomain
import com.waycool.data.repository.util.DomainMapper

class AddCropTypeDomainMapper : DomainMapper<SoilTypeDomain, AddCropTypeEntity> {
    override fun mapToDomain(dto: AddCropTypeEntity): SoilTypeDomain {
        return SoilTypeDomain(
            id = dto.id,
            isSelected = dto.isSelected,
            soil_type = dto.soil_type
        )
    }
    fun toDomainList(initial: List<AddCropTypeEntity>): List<SoilTypeDomain> {
        return initial.map {
            mapToDomain(it)
        }
    }
}