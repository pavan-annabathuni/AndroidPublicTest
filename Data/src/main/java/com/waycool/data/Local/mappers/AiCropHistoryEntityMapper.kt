package com.waycool.data.Local.mappers

import com.waycool.data.Local.Entity.AiCropHistoryEntity
import com.waycool.data.Local.utils.EntityMapper
import com.waycool.data.Network.NetworkModels.AiHistoryData

class AiCropHistoryEntityMapper : EntityMapper<AiCropHistoryEntity, AiHistoryData> {
    fun mapFromEntity(entity: AiCropHistoryEntity): AiHistoryData {
        return AiHistoryData(
            crop_id = entity.crop_id,
            disease_id = entity.disease_id,
            id = entity.id,
            image_url = entity.image_url,
            prediction = entity.prediction,
            probability = entity.probability,
            user_feedback = entity.user_feedback
        )
    }

    override fun mapToEntity(dto: AiHistoryData): AiCropHistoryEntity {
        return AiCropHistoryEntity(
            crop_id = dto.crop_id,
            disease_id = dto.disease_id,
            id = dto.id,
            image_url = dto.image_url,
            prediction = dto.prediction,
            probability = dto.probability,
            user_feedback = dto.user_feedback
        )
    }

    fun fromEntityList(initial: List<AiCropHistoryEntity>): List<AiHistoryData> {
        return initial.map { mapFromEntity(it) }
    }

    fun toEntityList(initial: List<AiHistoryData>): List<AiCropHistoryEntity> {
        return initial.map { mapToEntity(it) }
    }
}