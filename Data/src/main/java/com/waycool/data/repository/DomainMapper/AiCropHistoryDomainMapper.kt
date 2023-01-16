package com.waycool.data.repository.DomainMapper

import com.waycool.data.Local.Entity.AiCropHistoryEntity
import com.waycool.data.repository.domainModels.AiCropHistoryDomain
import com.waycool.data.repository.util.DomainMapper

class AiCropHistoryDomainMapper : DomainMapper<AiCropHistoryDomain, AiCropHistoryEntity> {
    override fun mapToDomain(dto: AiCropHistoryEntity): AiCropHistoryDomain {
        return AiCropHistoryDomain(
            crop_id = dto.cropId,
            disease_id = dto.diseaseId,
            id = dto.id,
            image_url = dto.imageUrl,
            prediction = dto.prediction,
            probability = dto.probability,
            user_feedback = dto.userFeedback,
            updated_at = dto.updatedAt,
            cropName = dto.cropName
        )
    }

    fun toDomainList(initial: List<AiCropHistoryEntity>): List<AiCropHistoryDomain> {
        return initial.map {
            mapToDomain(it) }
    }
}