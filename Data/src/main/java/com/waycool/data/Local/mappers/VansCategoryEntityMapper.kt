package com.waycool.data.Local.mappers

import com.waycool.data.Local.Entity.VansCategoryEntity
import com.waycool.data.Local.utils.EntityMapper
import com.waycool.data.Network.NetworkModels.VansCategoryData

class VansCategoryEntityMapper : EntityMapper<VansCategoryEntity, VansCategoryData> {
     fun mapFromEntity(entity: VansCategoryEntity): VansCategoryData {
        return VansCategoryData(
            id = entity.id,
            categoryName = entity.categoryName,
            vansType = entity.vansType,
            translation = entity.translation,
        )
    }

    override fun mapToEntity(dto: VansCategoryData): VansCategoryEntity {
        return VansCategoryEntity(
            id = dto.id,
            categoryName = dto.categoryName,
            vansType = dto.vansType,
            translation = dto.translation,
        )
    }

    fun fromEntityList(initial: List<VansCategoryEntity>): List<VansCategoryData> {

        return initial.map { mapFromEntity(it) }
    }

    fun toEntityList(initial: List<VansCategoryData>): List<VansCategoryEntity> {
        return initial.map { mapToEntity(it) }
    }
}