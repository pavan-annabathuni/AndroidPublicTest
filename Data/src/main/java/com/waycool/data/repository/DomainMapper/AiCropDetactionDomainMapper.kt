package com.waycool.data.repository.DomainMapper

import com.waycool.data.Network.NetworkModels.AiCropDetectionDTO
import com.waycool.data.Network.NetworkModels.AiCropDetectionData
import com.waycool.data.repository.domainModels.AiCropDetectionDomain
import com.waycool.data.repository.util.DomainMapper

class AiCropDetectionDomainMapper : DomainMapper<AiCropDetectionDomain, AiCropDetectionDTO> {
    override fun mapToDomain(dto: AiCropDetectionDTO): AiCropDetectionDomain {
        return AiCropDetectionDomain(
            id = dto.data?.id,
            imageUrl = dto.data?.imageUrl,
            prediction = dto.data?.prediction,
            probability = dto.data?.probability,
            cropId = dto.data?.cropId,
            diseaseId = dto.data?.diseaseId,
            message = dto.message,
            status = dto.status



        )
    }
}