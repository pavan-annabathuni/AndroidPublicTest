package com.waycool.data.Repository.DomainMapper

import com.waycool.data.Local.Entity.AiCropHistoryEntity
import com.waycool.data.Local.Entity.LanguageMasterEntity
import com.waycool.data.Repository.DomainModels.AiCropHistoryDomain
import com.waycool.data.Repository.DomainModels.LanguageMasterDomain
import com.waycool.data.Repository.util.DomainMapper

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
            updated_at = dto.updated_at
        )
    }

    fun toDomainList(initial: List<AiCropHistoryEntity>): List<AiCropHistoryDomain> {
        return initial.map {
            mapToDomain(it) }
    }
}