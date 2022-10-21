package com.waycool.data.Local.mappers

import com.waycool.data.Local.Entity.AddCropTypeEntity
import com.waycool.data.Local.Entity.CropCategoryEntity
import com.waycool.data.Local.utils.EntityMapper
import com.waycool.data.Network.NetworkModels.AddCropTypeData


class AddCropTypeEntityMapper : EntityMapper<AddCropTypeEntity, AddCropTypeData> {
    override fun mapToEntity(dto: AddCropTypeData): AddCropTypeEntity {
        return AddCropTypeEntity(
            id = dto.id,
            soil_type = dto.soilType
        )
    }
    fun toEntityList(initial: List<AddCropTypeData>): List<AddCropTypeEntity> {
        return initial.map {
            mapToEntity(it)
        }
    }


}