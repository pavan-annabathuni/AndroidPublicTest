package com.waycool.data.repository.DomainMapper

import com.waycool.data.Local.Entity.AddCropTypeEntity
import com.waycool.data.repository.domainModels.AddCropTypeDomain
import com.waycool.data.repository.util.DomainMapper

class AddCropTypeDomainMapper : DomainMapper<AddCropTypeDomain, AddCropTypeEntity> {
    override fun mapToDomain(dto: AddCropTypeEntity): AddCropTypeDomain {
        return AddCropTypeDomain(
            id = dto.id,
            isSelected = dto.isSelected,
            soil_type = dto.soil_type


        )
    }

    fun toDomainList(initial: List<AddCropTypeEntity>): List<AddCropTypeDomain> {
        return initial.map {
            mapToDomain(it)
        }
    }

}