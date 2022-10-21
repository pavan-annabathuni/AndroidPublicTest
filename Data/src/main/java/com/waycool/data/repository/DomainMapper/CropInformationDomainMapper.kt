package com.waycool.data.Repository.DomainMapper

import com.waycool.data.Local.Entity.AccountEntity
import com.waycool.data.Local.Entity.CropCategoryEntity
import com.waycool.data.Local.Entity.CropInformationEntityData
import com.waycool.data.Network.NetworkModels.CropInfoData
import com.waycool.data.Repository.DomainModels.AccountDomain
import com.waycool.data.Repository.DomainModels.CropCategoryMasterDomain
import com.waycool.data.Repository.DomainModels.CropInformationDomainData
import com.waycool.data.Repository.util.DomainMapper

class CropInformationDomainMapper: DomainMapper<CropInformationDomainData, CropInformationEntityData> {
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
