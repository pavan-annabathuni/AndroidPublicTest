package com.waycool.data.Local.mappers

import com.waycool.data.Local.Entity.CropCategoryEntity
import com.waycool.data.Local.Entity.CropMasterEntity
import com.waycool.data.Local.utils.EntityMapper
import com.waycool.data.Network.NetworkModels.CropCategoryData
import com.waycool.data.Network.NetworkModels.CropMasterData

class CropCategoryEnitiyMapper : EntityMapper<CropCategoryEntity, CropCategoryData> {
    fun mapFromEntity(entity: CropCategoryEntity): CropCategoryData {
        return CropCategoryData(
            id = entity.id,
            categoryName = entity.categoryName,
            translation = entity.translation
        )
    }

    override fun mapToEntity(dto: CropCategoryData): CropCategoryEntity {
        return CropCategoryEntity(
            id = dto.id,
            categoryName = dto.categoryName,
            translation = dto.translation
        )
    }

    fun fromEntityList(initial: List<CropCategoryEntity>): List<CropCategoryData> {
        return initial.map { mapFromEntity(it) }
    }

    fun toEntityList(initial: List<CropCategoryData>): List<CropCategoryEntity> {
        return initial.map { mapToEntity(it) }
    }
}