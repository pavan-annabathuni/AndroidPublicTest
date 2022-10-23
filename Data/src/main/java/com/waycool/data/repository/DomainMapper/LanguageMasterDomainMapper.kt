package com.waycool.data.repository.DomainMapper

import com.waycool.data.Local.Entity.LanguageMasterEntity
import com.waycool.data.repository.domainModels.LanguageMasterDomain
import com.waycool.data.repository.util.DomainMapper

class LanguageMasterDomainMapper : DomainMapper<LanguageMasterDomain, LanguageMasterEntity> {


    fun toDomainList(initial: List<LanguageMasterEntity>): List<LanguageMasterDomain> {
        return initial.map { mapToDomain(it) }
    }

    override fun mapToDomain(dto: LanguageMasterEntity): LanguageMasterDomain {
        return LanguageMasterDomain(
            id = dto.id,
            lang = dto.lang,
            langNative = dto.langNative,
            langCode = dto.langCode,
            langIcon = dto.langIcon
        )
    }


}