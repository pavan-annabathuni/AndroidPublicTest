package com.waycool.data.Local.mappers

import com.waycool.data.Local.Entity.AiCropHistoryEntity
import com.waycool.data.Local.utils.EntityMapper
import com.waycool.data.Network.NetworkModels.AiHistoryData

class AiCropHistoryEntityMapper : EntityMapper<AiCropHistoryEntity, AiHistoryData> {

    override fun mapToEntity(dto: AiHistoryData): AiCropHistoryEntity {
        return AiCropHistoryEntity(
            cropId = dto.cropId,
            diseaseId = dto.diseaseId,
            id = dto.id,
            imageUrl = dto.imageUrl,
            prediction = dto.prediction,
            probability = dto.probability,
            userFeedback = dto.userFeedback,
            cropName = dto.crop.cropName
        )
    }


    fun toEntityList(initial: List<AiHistoryData>): List<AiCropHistoryEntity> {
        return initial.map { mapToEntity(it) }
    }
}