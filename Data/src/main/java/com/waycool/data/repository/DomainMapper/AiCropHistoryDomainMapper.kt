package com.waycool.data.repository.DomainMapper

import com.waycool.data.Local.Entity.AiCropHistoryEntity
import com.waycool.data.repository.domainModels.AiCropHistoryDomain
import com.waycool.data.repository.util.DomainMapper

class AiCropHistoryDomainMapper : DomainMapper<AiCropHistoryDomain, AiCropHistoryEntity> {
    override fun mapToDomain(dto: AiCropHistoryEntity): AiCropHistoryDomain {
        return AiCropHistoryDomain(
            crop_id = dto.crop_id,
            disease_id = dto.disease_id,
            id = dto.id,
            image_url = dto.image_url,
            prediction = dto.prediction,
            probability = dto.probability,
            user_feedback = dto.user_feedback,
            updated_at = dto.updated_at,
            cropdata = dto.cropdata
        )
    }

    fun toDomainList(initial: List<AiCropHistoryEntity>): List<AiCropHistoryDomain> {
        return initial.map {
            mapToDomain(it) }
    }
}