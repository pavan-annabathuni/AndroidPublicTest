package com.waycool.data.Repository.DomainMapper

import com.waycool.data.Local.Entity.CropMasterEntity
import com.waycool.data.Local.Entity.LanguageMasterEntity
import com.waycool.data.Repository.DomainModels.CropMasterDomain
import com.waycool.data.Repository.DomainModels.LanguageMasterDomain
import com.waycool.data.Repository.util.DomainMapper

class CropMasterDomainMapper : DomainMapper<CropMasterDomain, CropMasterEntity> {
    override fun mapToDomain(dto: CropMasterEntity): CropMasterDomain {
        return CropMasterDomain(
            cropId = dto.cropId,
            cropName = dto.cropName,
            cropCategory_id = dto.cropCategory_id,
            cropLogo = dto.cropLogo,
            cropType = dto.cropType,
            translation = dto.translation
        )
    }

    fun toDomainList(initial: List<CropMasterEntity>): List<CropMasterDomain> {
        return initial.map { mapToDomain(it) }
    }
}