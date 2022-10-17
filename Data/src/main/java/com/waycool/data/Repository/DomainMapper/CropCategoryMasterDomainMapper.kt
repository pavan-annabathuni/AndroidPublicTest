package com.waycool.data.Repository.DomainMapper

import com.waycool.data.Local.Entity.CropCategoryEntity
import com.waycool.data.Local.Entity.LanguageMasterEntity
import com.waycool.data.Repository.DomainModels.CropCategoryMasterDomain
import com.waycool.data.Repository.DomainModels.LanguageMasterDomain
import com.waycool.data.Repository.util.DomainMapper

class CropCategoryMasterDomainMapper : DomainMapper<CropCategoryMasterDomain, CropCategoryEntity> {
    override fun mapToDomain(dto: CropCategoryEntity): CropCategoryMasterDomain {
        return CropCategoryMasterDomain(
            id = dto.id,
            categoryName = dto.categoryName,
            translation = dto.translation,
        )
    }

    fun toDomainList(initial: List<CropCategoryEntity>): List<CropCategoryMasterDomain> {
        return initial.map { mapToDomain(it) }
    }
}