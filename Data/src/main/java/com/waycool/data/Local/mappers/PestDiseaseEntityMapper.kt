package com.waycool.data.Local.mappers


import com.waycool.data.Local.Entity.PestDiseaseEntity
import com.waycool.data.Local.utils.EntityMapper
import com.waycool.data.Network.NetworkModels.PestDiseaseData

class PestDiseaseEntityMapper : EntityMapper<PestDiseaseEntity, PestDiseaseData> {
    override fun mapToEntity(dto: PestDiseaseData): PestDiseaseEntity {
        return PestDiseaseEntity(
            cropId = dto.cropId,
            diseaseName = dto.diseaseName,
            diseaseId = dto.diseaseId,
            thumb = dto.thumb,
            imageUrl = dto.imageUrl,
            audioUrl = dto.audioUrl,
            modeOfInfestation = dto.modeOfInfestation,
            symptoms = dto.symptoms,
            cultural = dto.recommendation?.cultural,
            biological = dto.recommendation?.biological,
            chemical = dto.recommendation?.chemical,
        )
    }

    fun toEntityList(initial: List<PestDiseaseData>): List<PestDiseaseEntity> {
        return initial.map { mapToEntity(it) }
    }
}