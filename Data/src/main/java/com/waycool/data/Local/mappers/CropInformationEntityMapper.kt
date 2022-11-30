package com.waycool.data.Local.mappers

import com.waycool.data.Local.Entity.CropInformationEntityData
import com.waycool.data.Local.utils.EntityMapper
import com.waycool.data.Network.NetworkModels.CropInfoData

class CropInformationEntityMapper: EntityMapper<CropInformationEntityData,CropInfoData> {

    override fun mapToEntity(dto: CropInfoData): CropInformationEntityData {
        return CropInformationEntityData(
            crop_id = dto.cropId,
            id = dto.id,
            label_name = dto.labelName,
            label_value = dto.labelValue,
            labelNameTag = dto.labelNameTag,
            labelValueTag = dto.labelValueTag
        )
    }

    fun toEntityList(initial: List<CropInfoData>): List<CropInformationEntityData> {
        return initial.map { mapToEntity(it) }
    }
}