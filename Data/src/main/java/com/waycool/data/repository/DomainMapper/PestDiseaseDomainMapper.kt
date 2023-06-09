package com.waycool.data.repository.DomainMapper

import com.waycool.data.Local.Entity.PestDiseaseEntity
import com.waycool.data.Local.utils.TypeConverter
import com.waycool.data.repository.domainModels.PestDiseaseDomain
import com.waycool.data.repository.util.DomainMapper

class PestDiseaseDomainMapper : DomainMapper<PestDiseaseDomain, PestDiseaseEntity> {
    override fun mapToDomain(dto: PestDiseaseEntity): PestDiseaseDomain {

        return PestDiseaseDomain(
            cropId = dto.cropId,
            diseaseName = dto.diseaseName,
            diseaseId = dto.diseaseId,
            thumb = dto.thumb,
            imageUrl = TypeConverter.convertStringToStringList(dto.imageUrl),
            audioUrl = dto.audioUrl,
            modeOfInfestation = dto.modeOfInfestation,
            symptoms = dto.symptoms,
            cultural = dto.cultural,
            biological = dto.biological,
            chemical = dto.chemical
        )

    }

    fun toDomainList(initial: List<PestDiseaseEntity>): List<PestDiseaseDomain> {
        return initial.map { mapToDomain(it) }
    }
}