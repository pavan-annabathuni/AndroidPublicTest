package com.waycool.data.Repository.DomainMapper

import com.waycool.data.Local.Entity.LanguageMasterEntity
import com.waycool.data.Local.Entity.ModuleMasterEntity
import com.waycool.data.Repository.DomainModels.LanguageMasterDomain
import com.waycool.data.Repository.DomainModels.ModuleMasterDomain
import com.waycool.data.Repository.util.DomainMapper

class ModuleMasterDomainMapper : DomainMapper<ModuleMasterDomain, ModuleMasterEntity> {
    override fun mapToDomain(dto: ModuleMasterEntity): ModuleMasterDomain {

        return ModuleMasterDomain(
            id = dto.id,
            moduleType = dto.moduleType,
            moduleIcon = dto.moduleIcon,
            moduleDesc = dto.moduleDesc,
            Premium = dto.Premium,
            tittle = dto.tittle,
            price = dto.price,
            audioURl = dto.audioURl,
            translation = dto.translation,
        )
    }

    fun toDomainList(initial: List<ModuleMasterEntity>): List<ModuleMasterDomain> {
        return initial.map { mapToDomain(it) }
    }
}