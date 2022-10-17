package com.waycool.data.Repository.DomainMapper

import com.waycool.data.Local.Entity.LanguageMasterEntity
import com.waycool.data.Local.Entity.VansCategoryEntity
import com.waycool.data.Network.NetworkModels.VansCategoryData
import com.waycool.data.Repository.DomainModels.LanguageMasterDomain
import com.waycool.data.Repository.DomainModels.VansCategoryDomain
import com.waycool.data.Repository.util.DomainMapper

class VansCategoryDomainMapper : DomainMapper<VansCategoryDomain, VansCategoryEntity> {
    override fun mapToDomain(dto: VansCategoryEntity): VansCategoryDomain {
        return VansCategoryDomain(
            id = dto.id,
            categoryName = dto.categoryName,
            vansType = dto.vansType,
            translation = dto.translation,
        )
    }

    fun toDomainList(initial: List<VansCategoryEntity>): List<VansCategoryDomain> {
        return initial.map { mapToDomain(it) }
    }
}