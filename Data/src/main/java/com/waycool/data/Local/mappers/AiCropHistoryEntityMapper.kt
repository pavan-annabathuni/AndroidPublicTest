package com.waycool.data.Local.mappers

import com.waycool.data.Local.Entity.AiCropHistoryEntity
import com.waycool.data.Local.utils.EntityMapper
import com.waycool.data.Network.NetworkModels.AiHistoryData

class AiCropHistoryEntityMapper : EntityMapper<AiCropHistoryEntity, AiHistoryData> {
    fun mapFromEntity(entity: AiCropHistoryEntity): AiHistoryData {
        return AiHistoryData(
            cropId = entity.cropId,
            diseaseId = entity.diseaseId,
            id = entity.id,
            imageUrl = entity.imageUrl,
            prediction = entity.prediction,
            probability = entity.probability,
            userFeedback = entity.userFeedback,
           crop = entity.crop
        )
    }

    override fun mapToEntity(dto: AiHistoryData): AiCropHistoryEntity {
        return AiCropHistoryEntity(
            cropId = dto.cropId,
            diseaseId = dto.diseaseId,
            id = dto.id,
            imageUrl = dto.imageUrl,
            prediction = dto.prediction,
            probability = dto.probability,
            userFeedback = dto.userFeedback,
            crop = dto.crop
        )
    }

    fun fromEntityList(initial: List<AiCropHistoryEntity>): List<AiHistoryData> {
        return initial.map { mapFromEntity(it) }
    }

    fun toEntityList(initial: List<AiHistoryData>): List<AiCropHistoryEntity> {
        return initial.map { mapToEntity(it) }
    }
}