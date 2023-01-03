package com.waycool.data.repository.DomainMapper

import com.waycool.data.Local.Entity.CropCategoryEntity
import com.waycool.data.repository.domainModels.CropCategoryMasterDomain
import com.waycool.data.repository.util.DomainMapper

class CropCategoryMasterDomainMapper : DomainMapper<CropCategoryMasterDomain, CropCategoryEntity> {
    override fun mapToDomain(dto: CropCategoryEntity): CropCategoryMasterDomain {
        return CropCategoryMasterDomain(
            id = dto.id,
            categoryName = dto.categoryName,
            translation = dto.translation,
            categoryTagName = dto.categoryTagName
        )
    }

    fun toDomainList(initial: List<CropCategoryEntity>): List<CropCategoryMasterDomain> {
        return initial.map { mapToDomain(it) }
    }
}