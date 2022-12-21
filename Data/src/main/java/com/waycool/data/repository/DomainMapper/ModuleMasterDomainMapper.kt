package com.waycool.data.repository.DomainMapper

import com.waycool.data.Local.Entity.ModuleMasterEntity
import com.waycool.data.repository.domainModels.ModuleMasterDomain
import com.waycool.data.repository.util.DomainMapper

class ModuleMasterDomainMapper : DomainMapper<ModuleMasterDomain, ModuleMasterEntity> {
    override fun mapToDomain(dto: ModuleMasterEntity): ModuleMasterDomain {

        return ModuleMasterDomain(
            id = dto.id,
            moduleType = dto.moduleType,
            moduleIcon = dto.moduleIcon,
            moduleDesc = dto.moduleDesc,
            subscription = dto.subscription,
            title = dto.title,
            audioUrl = dto.audioUrl,
            mobileDisplay = dto.mobileDisplay,
            translation = dto.translation,
            updatedAt = dto.updatedAt
        )
    }

    fun toDomainList(initial: List<ModuleMasterEntity>): List<ModuleMasterDomain> {
        return initial.map { mapToDomain(it) }
    }
}