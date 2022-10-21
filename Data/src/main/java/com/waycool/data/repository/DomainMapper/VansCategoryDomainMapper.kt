package com.waycool.data.repository.DomainMapper

import com.waycool.data.Local.Entity.VansCategoryEntity
import com.waycool.data.repository.domainModels.VansCategoryDomain
import com.waycool.data.repository.util.DomainMapper

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