package com.waycool.data.Repository.DomainMapper

import com.waycool.data.Network.NetworkModels.AiCropDetectionData
import com.waycool.data.Repository.DomainModels.AiCropDetectionDomain
import com.waycool.data.Repository.util.DomainMapper

class AiCropDetectionDomainMapper : DomainMapper<AiCropDetectionDomain, AiCropDetectionData> {
    override fun mapToDomain(dto: AiCropDetectionData): AiCropDetectionDomain {
        return AiCropDetectionDomain(
            id = dto.id,
            imageUrl = dto.imageUrl,
            prediction = dto.prediction,
            probability = dto.probability,
            cropId = dto.cropId,
            diseaseId = dto.diseaseId
        )
    }
}