package com.waycool.data.Local.mappers

import com.waycool.data.Local.Entity.LanguageMasterEntity
import com.waycool.data.Local.utils.EntityMapper
import com.waycool.data.Network.NetworkModels.LanguageData

class LangugeMasterEntityMapper : EntityMapper<LanguageMasterEntity, LanguageData> {

     fun mapFromEntity(entity: LanguageMasterEntity): LanguageData {
        return LanguageData(
            id = entity.id,
            lang = entity.lang,
            langNative = entity.langNative,
            langCode = entity.langCode,
            langIcon = entity.langIcon
        )
    }

    override fun mapToEntity(dto: LanguageData): LanguageMasterEntity {
        return LanguageMasterEntity(
            id = dto.id,
            lang = dto.lang,
            langNative = dto.langNative,
            langCode = dto.langCode,
            langIcon = dto.langIcon
        )
    }

    fun fromEntityList(initial: List<LanguageMasterEntity>): List<LanguageData> {
        return initial.map { mapFromEntity(it) }
    }

    fun toEntityList(initial: List<LanguageData>): List<LanguageMasterEntity> {
        return initial.map { mapToEntity(it) }
    }


}