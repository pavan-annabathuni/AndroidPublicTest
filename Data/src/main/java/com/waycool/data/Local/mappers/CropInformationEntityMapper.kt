package com.waycool.data.Local.mappers

import com.waycool.data.Local.Entity.CropInformationEntityData
import com.waycool.data.Local.Entity.PestDiseaseEntity
import com.waycool.data.Local.utils.EntityMapper
import com.waycool.data.Network.NetworkModels.CropInfo
import com.waycool.data.Network.NetworkModels.CropInfoData

class CropInformationEntityMapper: EntityMapper<CropInformationEntityData,CropInfoData> {
    fun mapFromEntity(entity: CropInformationEntityData): CropInfoData {
        return CropInfoData(
            crop_id = entity.crop_id,
            id = entity.id,
            label_name = entity.label_name,
            label_value = entity.label_value
        )
    }

    override fun mapToEntity(dto: CropInfoData): CropInformationEntityData {
        return CropInformationEntityData(
            crop_id = dto.crop_id,
            id = dto.id,
            label_name = dto.label_name,
            label_value = dto.label_value
        )
    }

    fun toEntityList(initial: List<CropInfoData>): List<CropInformationEntityData> {
        return initial.map { mapToEntity(it) }
    }
}