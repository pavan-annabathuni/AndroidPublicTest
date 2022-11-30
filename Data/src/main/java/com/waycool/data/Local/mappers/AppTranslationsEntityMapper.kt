package com.waycool.data.Local.mappers

import com.waycool.data.Local.Entity.AppTranslationsEntity
import com.waycool.data.Local.utils.EntityMapper
import com.waycool.data.Network.NetworkModels.AppTranslationsNetwork

class AppTranslationsEntityMapper : EntityMapper<AppTranslationsEntity, AppTranslationsNetwork> {
    override fun mapToEntity(dto: AppTranslationsNetwork): AppTranslationsEntity {
        return AppTranslationsEntity(
            id = dto.id,
            appKey = dto.appKey,
            appValue = dto.appValue,
            createdAt = dto.createdAt,
            appValueTag = dto.appValueTag
        )
    }

    fun toEntityList(initial: List<AppTranslationsNetwork>): List<AppTranslationsEntity> {
        return initial.map { mapToEntity(it) }
    }
}