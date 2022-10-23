package com.waycool.data.repository.DomainMapper

import com.waycool.data.Local.Entity.CropInformationEntityData
import com.waycool.data.repository.domainModels.CropInformationDomainData
import com.waycool.data.repository.util.DomainMapper

class CropInformationDomainMapper:
    DomainMapper<CropInformationDomainData, CropInformationEntityData> {
    override fun mapToDomain(dto: CropInformationEntityData): CropInformationDomainData {
        return CropInformationDomainData(
            crop_id = dto.crop_id,
            id = dto.id,
            label_name = dto.label_name,
            label_value = dto.label_value
        )
    }
//    class CropInformationDataDomainMapper: DomainMapper<CropInformationDomainData, CropInformationEntityData> {
//        override fun mapToDomain(dto: CropInformationEntityData): CropInformationDomainData {
//            return CropInformationDomainData(
//                crop_id = dto.crop_id,
//                id = dto.id,
//                label_name = dto.label_name,
//                label_value = dto.label_value
//            )
//        }
        fun toDomainList(initial: List<CropInformationEntityData>): List<CropInformationDomainData> {
            return initial.map { mapToDomain(it) }
    }

    }
